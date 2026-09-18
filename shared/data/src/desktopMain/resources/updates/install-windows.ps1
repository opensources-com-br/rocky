param(
    [string]$Package,
    [string]$Target,
    [int]$ParentId,
    [string]$Job,
    [string]$ExpectedHash,
    [string]$NativeVersion,
    [string]$ReleaseVersion
)
$ErrorActionPreference = 'Stop'
$upgradeCode = '{CD761319-DDDF-439F-BEAF-9616ED84E4AF}'
$stopped = $false
$finished = $false
$packageLock = $null
$executable = Join-Path $Target 'Rocky.exe'
$utf8 = New-Object System.Text.UTF8Encoding($false)

function Write-Status([string]$Value) {
    [IO.File]::WriteAllText((Join-Path $Job 'status'), $Value, $utf8)
}

function Assert-NotCancelled {
    if (Test-Path -LiteralPath (Join-Path $Job 'cancel')) { throw 'Update cancelled.' }
}

function Get-MsiProperty($Database, [string]$Name) {
    $view = $Database.OpenView("SELECT ``Value`` FROM ``Property`` WHERE ``Property``='$Name'")
    try {
        $view.Execute()
        $record = $view.Fetch()
        if ($null -eq $record) { throw "Missing MSI property: $Name" }
        try { return $record.StringData(1) }
        finally { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($record) }
    } finally {
        $view.Close()
        [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($view)
    }
}

try {
    [IO.File]::WriteAllText((Join-Path $Job 'helper.pid'), "$PID", $utf8)
    if ($ParentId -le 1 -or $ExpectedHash -cnotmatch '^[a-f0-9]{64}$') { throw 'Invalid update arguments.' }
    if ($NativeVersion -notmatch '^\d+\.\d+\.\d+$') { throw 'Invalid native version.' }
    $packageItem = Get-Item -LiteralPath $Package
    $targetItem = Get-Item -LiteralPath $Target
    if (($packageItem.Attributes -band [IO.FileAttributes]::ReparsePoint) -or
        ($targetItem.Attributes -band [IO.FileAttributes]::ReparsePoint)) { throw 'Update paths cannot be links.' }
    if ($packageItem.Extension -ine '.msi' -or -not (Test-Path -LiteralPath $executable)) { throw 'Invalid installed application.' }
    $packageLock = [IO.File]::Open($Package, 'Open', 'Read', 'Read')
    if ((Get-FileHash -LiteralPath $Package -Algorithm SHA256).Hash -ine $ExpectedHash) { throw 'Installer checksum mismatch.' }

    $installer = New-Object -ComObject WindowsInstaller.Installer
    $database = $null
    try {
        $database = $installer.OpenDatabase($Package, 0)
        if ((Get-MsiProperty $database 'UpgradeCode') -ine $upgradeCode) { throw 'Incorrect MSI upgrade identity.' }
        if ((Get-MsiProperty $database 'ProductName') -cne 'Rocky') { throw 'Incorrect MSI product.' }
        if ((Get-MsiProperty $database 'ProductVersion') -ne $NativeVersion) { throw 'Incorrect MSI version.' }
        $related = $installer.RelatedProducts($upgradeCode)
        $matched = $false
        foreach ($product in $related) {
            $location = $installer.ProductInfo($product, 'InstallLocation')
            if ($location -and [IO.Path]::GetFullPath($location).TrimEnd('\') -ieq $Target.TrimEnd('\')) {
                $installedVersion = $installer.ProductInfo($product, 'VersionString')
                if ([version]$NativeVersion -le [version]$installedVersion) { throw 'Native update version must increase.' }
                $matched = $true
            }
        }
        if ([Runtime.InteropServices.Marshal]::IsComObject($related)) {
            [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($related)
        }
        if (-not $matched) { throw 'Rocky must be installed using its official MSI installer.' }
    } finally {
        if ($null -ne $database) { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($database) }
        [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($installer)
    }

    $currentSignature = Get-AuthenticodeSignature -LiteralPath $executable
    if ($null -ne $currentSignature.SignerCertificate) {
        $signature = Get-AuthenticodeSignature -LiteralPath $Package
