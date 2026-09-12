# Requires a code-signing certificate/private key in the current user's certificate store.
$ErrorActionPreference = 'Stop'
if (-not $env:ROCKY_WINDOWS_CERT_THUMBPRINT) { throw 'Set ROCKY_WINDOWS_CERT_THUMBPRINT for an installed signing certificate.' }
$rockyRoot = Split-Path -Parent $PSScriptRoot
$rockySignTool = if ($env:ROCKY_SIGNTOOL) { $env:ROCKY_SIGNTOOL } else { (Get-Command signtool.exe -ErrorAction Stop).Source }
$rockyTimestampUrl = if ($env:ROCKY_TIMESTAMP_URL) { $env:ROCKY_TIMESTAMP_URL } else { 'http://timestamp.digicert.com' }
$rockyPackages = @(Get-ChildItem "$rockyRoot/apps/desktop/build/compose/binaries/main/msi/*.msi", "$rockyRoot/apps/desktop/build/compose/binaries/main/exe/*.exe")
if ($rockyPackages.Count -ne 2) { throw 'Expected exactly one MSI and one EXE for the candidate.' }
foreach ($rockyPackage in $rockyPackages) {
    & $rockySignTool sign /sha1 $env:ROCKY_WINDOWS_CERT_THUMBPRINT /fd SHA256 /tr $rockyTimestampUrl /td SHA256 $rockyPackage.FullName
    if ($LASTEXITCODE -ne 0) { throw 'Signing failed.' }
    & $rockySignTool verify /pa /all $rockyPackage.FullName
    if ($LASTEXITCODE -ne 0) { throw 'Signature verification failed.' }
}
python "$rockyRoot/scripts/release_metadata.py"
if ($LASTEXITCODE -ne 0) { throw 'Release metadata generation failed.' }
