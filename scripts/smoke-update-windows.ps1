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
