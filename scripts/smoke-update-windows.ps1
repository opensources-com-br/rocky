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
