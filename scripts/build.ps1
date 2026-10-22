param(
    [ValidateSet('modern', '1.7.10')]
    [string]$Target = 'modern'
)

$ErrorActionPreference = 'Stop'
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$projectDirectory = $repositoryRoot

if ($Target -eq '1.7.10') {
    $projectDirectory = Join-Path $repositoryRoot 'versions\1.7.10-forge'
}

Push-Location $projectDirectory
try {
    & .\gradlew.bat build
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
} finally {
    Pop-Location
}
