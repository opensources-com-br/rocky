# Patch before signing: jpackage otherwise removes the old version outside the MSI transaction.
param(
    [Parameter(Mandatory = $true, ParameterSetName = 'File')][string]$MsiPath,
    [Parameter(Mandatory = $true, ParameterSetName = 'Directory')][string]$Directory
)
$ErrorActionPreference = 'Stop'
if ($PSCmdlet.ParameterSetName -eq 'Directory') {
    $packages = @(Get-ChildItem -LiteralPath $Directory -Filter '*.msi' -File | Sort-Object Name)
    if ($packages.Count -eq 0) { throw 'No MSI packages found to enable upgrade rollback.' }
    foreach ($item in $packages) { & $PSCommandPath -MsiPath $item.FullName }
    return
}
$package = Get-Item -LiteralPath $MsiPath
if ($package.Extension -ine '.msi') { throw 'Expected an MSI package.' }
$installer = New-Object -ComObject WindowsInstaller.Installer
$database = $null

function Read-Sequence([string]$Action) {
    $view = $database.OpenView("SELECT ``Sequence`` FROM ``InstallExecuteSequence`` WHERE ``Action``='$Action'")
    try {
