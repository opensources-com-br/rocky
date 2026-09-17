$ErrorActionPreference = 'Stop'
$msi = (Get-ChildItem 'apps/desktop/build/compose/binaries/main/msi' -Filter '*.msi' | Select-Object -First 1).FullName
$target = Join-Path $env:RUNNER_TEMP 'Rocky instalação smoke'
New-Item -ItemType Directory -Force -Path $target | Out-Null

$arguments = @('/a', "`"$msi`"", '/qn', "TARGETDIR=`"$target`"")
$process = Start-Process msiexec.exe -Wait -PassThru -ArgumentList $arguments
if ($process.ExitCode -ne 0) { throw "MSI extraction failed with code $($process.ExitCode)" }
if (-not (Get-ChildItem $target -Recurse -Filter 'Rocky.exe')) { throw 'Rocky launcher is missing' }
$runtime = Get-ChildItem $target -Recurse -Filter 'release' | Where-Object FullName -Match '[\\/]runtime[\\/]'
if (-not $runtime) { throw 'Bundled Java runtime is missing' }
