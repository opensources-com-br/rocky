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
        $view.Execute()
        $record = $view.Fetch()
        if ($null -eq $record) { throw "Required installer action is absent: $Action" }
        try { return [int]$record.IntegerData(1) }
        finally { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($record) }
    } finally {
        $view.Close()
        [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($view)
    }
}

try {
    $database = $installer.OpenDatabase($package.FullName, 1)
    $initialize = Read-Sequence 'InstallInitialize'
    $remove = Read-Sequence 'RemoveExistingProducts'
    $finalize = Read-Sequence 'InstallFinalize'
    $sequence = $initialize + 1
    if ($initialize -le 0 -or $sequence -ge $finalize) { throw 'Unexpected installation transaction boundaries.' }
    if ($remove -ne $sequence) {
        $collision = $database.OpenView("SELECT ``Action`` FROM ``InstallExecuteSequence`` WHERE ``Sequence``=$sequence")
