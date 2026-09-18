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

function Assert-ExclusiveApplication {
    foreach ($instance in @(Get-Process -Name 'Rocky' -ErrorAction SilentlyContinue)) {
        if ($instance.Id -eq $ParentId) { continue }
        $path = $instance.Path
        if (-not $path) { throw 'Cannot verify another Rocky process. Close other Rocky instances before updating.' }
        if ([IO.Path]::GetFullPath($path) -ieq [IO.Path]::GetFullPath($executable)) {
            throw 'Close other instances of this Rocky installation before updating.'
        }
    }
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
        if ($signature.Status -ne 'Valid' -or
            $signature.SignerCertificate.Subject -cne $currentSignature.SignerCertificate.Subject) {
            throw 'The update must be signed by the installed application publisher.'
        }
    }
    Assert-NotCancelled
    Write-Status 'ready'
    [IO.File]::WriteAllText((Join-Path $Job 'ready'), 'ready', $utf8)
    $deadline = [DateTime]::UtcNow.AddSeconds(120)
    while (Get-Process -Id $ParentId -ErrorAction SilentlyContinue) {
        Assert-NotCancelled
        if ([DateTime]::UtcNow -gt $deadline) { throw 'Rocky did not exit before the update timeout.' }
        Start-Sleep -Milliseconds 200
    }
    Assert-NotCancelled
    $stopped = $true
    Write-Status 'installing'
    $msiexec = Join-Path $env:SystemRoot 'System32\msiexec.exe'
    $log = Join-Path $Job 'msi.log'
    $arguments = @('/i', "`"$Package`"", '/passive', '/norestart', '/L*v', "`"$log`"", "INSTALLDIR=`"$Target`"")
    $process = Start-Process -FilePath $msiexec -Verb RunAs -ArgumentList $arguments -Wait -PassThru
    if ($process.ExitCode -notin @(0, 3010)) { throw "Windows Installer failed with code $($process.ExitCode)." }
    $config = Join-Path $Target 'app\Rocky.cfg'
    $versionLine = "java-options=-Drocky.version=$ReleaseVersion"
    if (-not (Test-Path -LiteralPath $executable) -or
        -not ((Get-Content -LiteralPath $config) -ccontains $versionLine)) { throw 'Updated application verification failed.' }
    if ($process.ExitCode -eq 3010) { Write-Status 'restart-required' } else { Write-Status 'installed' }
    Start-Process -FilePath $executable -WorkingDirectory $Target
    $finished = $true
} catch {
    Write-Output $_.Exception.ToString()
    if (Test-Path -LiteralPath (Join-Path $Job 'cancel')) { Write-Status 'cancelled' } else { Write-Status 'failed' }
    if ($stopped -and (Test-Path -LiteralPath $executable)) {
        try { Start-Process -FilePath $executable -WorkingDirectory $Target } catch { Write-Output $_ }
    }
} finally {
    if ($null -ne $packageLock) { $packageLock.Dispose() }
}
if (-not $finished) { exit 1 }
