# Exercise the real Windows updater with two tiny jpackage installations.
$ErrorActionPreference = 'Stop'
$fixture = Join-Path $env:RUNNER_TEMP 'Rocky update smoke'
$inputDirectory = Join-Path $fixture 'input'
$packages = Join-Path $fixture 'packages'
$result = Join-Path $fixture 'started.txt'
$target = Join-Path $env:ProgramFiles 'Rocky'
$job = Join-Path $fixture 'job'
New-Item -ItemType Directory -Force $inputDirectory, $packages, $job | Out-Null
$source = @'
import java.nio.file.*;
public class Main {
    public static void main(String[] args) throws Exception {
        Files.writeString(Path.of(System.getProperty("rocky.smoke.result")), System.getProperty("rocky.version"));
    }
}
'@
[IO.File]::WriteAllText((Join-Path $fixture 'Main.java'), $source)
& "$env:JAVA_HOME/bin/javac.exe" -d $inputDirectory (Join-Path $fixture 'Main.java')
if ($LASTEXITCODE -ne 0) { throw 'Fixture compilation failed' }
& "$env:JAVA_HOME/bin/jar.exe" --create --file (Join-Path $inputDirectory 'fixture.jar') -C $inputDirectory 'Main.class'
if ($LASTEXITCODE -ne 0) { throw 'Fixture JAR failed' }
foreach ($version in @('1.0.1', '1.0.2')) {
    & "$env:JAVA_HOME/bin/jpackage.exe" --type msi --name Rocky --app-version $version `
        --input $inputDirectory --main-jar fixture.jar --main-class Main --dest $packages `
        --win-upgrade-uuid 'CD761319-DDDF-439F-BEAF-9616ED84E4AF' --win-menu --win-shortcut `
        --java-options "-Drocky.version=$version-alpha.1" --java-options "'-Drocky.smoke.result=$result'" --add-modules java.base
    if ($LASTEXITCODE -ne 0) { throw "Fixture packaging failed: $version" }
    $msi = Join-Path $packages "Rocky-$version.msi"
    & "$PSScriptRoot/enable-windows-upgrade-rollback.ps1" -MsiPath $msi
    & "$PSScriptRoot/enable-windows-upgrade-rollback.ps1" -MsiPath $msi
}
$baseline = Join-Path $packages 'Rocky-1.0.1.msi'
$candidate = Join-Path $packages 'Rocky-1.0.2.msi'
$parent = $null
$helper = $null
try {
    $install = Start-Process msiexec.exe -ArgumentList @('/i', "`"$baseline`"", '/qn', '/norestart') -Wait -PassThru
    if ($install.ExitCode -ne 0) { throw "Baseline installation failed: $($install.ExitCode)" }
    if (-not (Test-Path (Join-Path $target 'Rocky.exe'))) { throw 'Baseline launcher missing' }
    $parent = Start-Process powershell.exe -ArgumentList @('-NoProfile', '-Command', 'Start-Sleep 120') -PassThru
    $script = Join-Path $PSScriptRoot '../shared/data/src/desktopMain/resources/updates/install-windows.ps1'
    $hash = (Get-FileHash $candidate -Algorithm SHA256).Hash.ToLowerInvariant()
    $arguments = @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', "`"$script`"", "`"$candidate`"",
        "`"$target`"", $parent.Id, "`"$job`"", $hash, '1.0.2', '1.0.2-alpha.1')
    $helper = Start-Process powershell.exe -ArgumentList $arguments -PassThru `
        -RedirectStandardOutput (Join-Path $job 'stdout.log') -RedirectStandardError (Join-Path $job 'stderr.log')
    $deadline = [DateTime]::UtcNow.AddSeconds(60)
    while (-not (Test-Path (Join-Path $job 'ready'))) {
        if ($helper.HasExited -or [DateTime]::UtcNow -gt $deadline) { throw 'Updater did not become ready' }
        Start-Sleep -Milliseconds 200
    }
    $baselineConfig = Get-Content (Join-Path $target 'app/Rocky.cfg')
    if ($baselineConfig -notcontains 'java-options=-Drocky.version=1.0.1-alpha.1') { throw 'App was replaced before exiting' }
    Stop-Process -Id $parent.Id
    if (-not $helper.WaitForExit(120000) -or $helper.ExitCode -ne 0) { throw 'Native upgrade failed' }
    $deadline = [DateTime]::UtcNow.AddSeconds(30)
    while (-not (Test-Path $result)) {
        if ([DateTime]::UtcNow -gt $deadline) { throw 'Updated application did not restart' }
        Start-Sleep -Milliseconds 200
