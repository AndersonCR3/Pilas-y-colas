param(
    [string]$MavenVersion = "3.9.6",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Add-UserPathIfMissing {
    param([string]$PathToAdd)

    $userPath = [Environment]::GetEnvironmentVariable("Path", "User")
    if ([string]::IsNullOrWhiteSpace($userPath)) {
        $userPath = ""
    }

    $alreadyExists = $false
    $segments = $userPath.Split(';') | ForEach-Object { $_.Trim() } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    foreach ($segment in $segments) {
        if ($segment.TrimEnd('\') -ieq $PathToAdd.TrimEnd('\')) {
            $alreadyExists = $true
            break
        }
    }

    if (-not $alreadyExists) {
        $newUserPath = if ([string]::IsNullOrWhiteSpace($userPath)) { $PathToAdd } else { "$userPath;$PathToAdd" }
        [Environment]::SetEnvironmentVariable("Path", $newUserPath, "User")
        Write-Info "Path de usuario actualizado con: $PathToAdd"
    }
    else {
        Write-Info "El Path de usuario ya contiene: $PathToAdd"
    }
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$toolsDir = Join-Path $env:USERPROFILE "tools"
$mavenDir = Join-Path $toolsDir "apache-maven-$MavenVersion"
$mavenBin = Join-Path $mavenDir "bin"
$zipPath = Join-Path $env:TEMP "apache-maven-$MavenVersion-bin.zip"
$downloadUrls = @(
    "https://dlcdn.apache.org/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip",
    "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip",
    "https://downloads.apache.org/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
)

Write-Info "Proyecto: $projectRoot"
Write-Info "Versión Maven solicitada: $MavenVersion"

if (-not (Test-Path $mavenBin)) {
    $downloaded = $false
    foreach ($downloadUrl in $downloadUrls) {
        try {
            Write-Info "Intentando descarga desde: $downloadUrl"
            Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath -UseBasicParsing
            $downloaded = $true
            break
        }
        catch {
            Write-Host "[WARN] Falló descarga desde: $downloadUrl" -ForegroundColor Yellow
        }
    }

    if (-not $downloaded) {
        throw "No fue posible descargar Maven. Verifica internet o cambia -MavenVersion."
    }

    if (-not (Test-Path $toolsDir)) {
        New-Item -ItemType Directory -Path $toolsDir | Out-Null
    }

    Write-Info "Extrayendo Maven en: $toolsDir"
    Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
}
else {
    Write-Info "Maven ya existe en: $mavenDir"
}

[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenDir, "User")
Add-UserPathIfMissing -PathToAdd $mavenBin

$env:MAVEN_HOME = $mavenDir
if (-not ($env:Path.Split(';') | Where-Object { $_.TrimEnd('\') -ieq $mavenBin.TrimEnd('\') })) {
    $env:Path = "$mavenBin;$env:Path"
}

Write-Info "Verificando Maven en la sesión actual..."
mvn -v

if (-not $SkipBuild) {
    Write-Info "Ejecutando build Maven: mvn clean package"
    Push-Location $projectRoot
    try {
        mvn clean package
    }
    finally {
        Pop-Location
    }

    Write-Info "Build finalizado. Revisa la carpeta target para el JAR."
}
else {
    Write-Info "Instalación/configuración completada. Se omitió el build por -SkipBuild."
}
