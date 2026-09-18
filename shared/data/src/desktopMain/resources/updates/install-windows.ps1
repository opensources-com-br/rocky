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
