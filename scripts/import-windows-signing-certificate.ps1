# Optional Authenticode signing for publishers with a Windows certificate.
$ErrorActionPreference = 'Stop'
if (-not $env:ROCKY_WINDOWS_CERTIFICATE_BASE64) { exit 0 }
if (-not $env:ROCKY_WINDOWS_CERTIFICATE_PASSWORD) { throw 'Missing Windows certificate password' }
$certificatePath = Join-Path $env:RUNNER_TEMP 'rocky-signing.pfx'
try {
    [IO.File]::WriteAllBytes($certificatePath, [Convert]::FromBase64String($env:ROCKY_WINDOWS_CERTIFICATE_BASE64))
    $password = ConvertTo-SecureString $env:ROCKY_WINDOWS_CERTIFICATE_PASSWORD -AsPlainText -Force
    $certificate = Import-PfxCertificate -FilePath $certificatePath -Password $password -CertStoreLocation Cert:\CurrentUser\My
    $signer = @($certificate | Where-Object HasPrivateKey)
    if ($signer.Count -ne 1) { throw 'Expected one Windows signing identity' }
    "ROCKY_WINDOWS_CERT_THUMBPRINT=$($signer[0].Thumbprint)" >> $env:GITHUB_ENV
    $kits = Join-Path ${env:ProgramFiles(x86)} 'Windows Kits/10/bin'
    $tool = Get-ChildItem "$kits/*/x64/signtool.exe" | Sort-Object FullName -Descending | Select-Object -First 1
    if (-not $tool) { throw 'Windows SDK signtool was not found' }
    "ROCKY_SIGNTOOL=$($tool.FullName)" >> $env:GITHUB_ENV
} finally {
    Remove-Item $certificatePath -Force -ErrorAction SilentlyContinue
}
