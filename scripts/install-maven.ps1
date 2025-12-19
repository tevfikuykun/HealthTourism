# Maven Installation Script for Windows
# This script downloads and installs Apache Maven

Write-Host "Maven Installation Script" -ForegroundColor Cyan
Write-Host "============================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is already installed
$mavenCheck = Get-Command mvn -ErrorAction SilentlyContinue
if ($mavenCheck) {
    Write-Host "Maven is already installed!" -ForegroundColor Green
    mvn --version
    exit 0
}

Write-Host "Downloading Apache Maven..." -ForegroundColor Yellow

# Maven download URL
$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$downloadPath = "$env:TEMP\apache-maven-$mavenVersion-bin.zip"
$installPath = "$env:USERPROFILE\maven"

# Create install directory
if (-not (Test-Path $installPath)) {
    New-Item -ItemType Directory -Path $installPath -Force | Out-Null
}

# Download Maven
try {
    Write-Host "Downloading from: $mavenUrl" -ForegroundColor Gray
    Invoke-WebRequest -Uri $mavenUrl -OutFile $downloadPath -UseBasicParsing
    Write-Host "Download completed!" -ForegroundColor Green
} catch {
    Write-Host "Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please download manually from:" -ForegroundColor Yellow
    Write-Host "https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Then extract to: $installPath" -ForegroundColor Yellow
    exit 1
}

# Extract Maven
Write-Host "Extracting Maven..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $downloadPath -DestinationPath $installPath -Force
    Write-Host "Extraction completed!" -ForegroundColor Green
} catch {
    Write-Host "Extraction failed: $_" -ForegroundColor Red
    exit 1
}

# Set Environment Variables
Write-Host "Setting environment variables..." -ForegroundColor Yellow

$mavenHome = "$installPath\apache-maven-$mavenVersion"

# Set MAVEN_HOME
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, [System.EnvironmentVariableTarget]::User)

# Add to PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", [System.EnvironmentVariableTarget]::User)
$mavenBin = "$mavenHome\bin"

if ($currentPath -notlike "*$mavenBin*") {
    $newPath = "$currentPath;$mavenBin"
    [System.Environment]::SetEnvironmentVariable("Path", $newPath, [System.EnvironmentVariableTarget]::User)
    Write-Host "PATH updated!" -ForegroundColor Green
} else {
    Write-Host "PATH already contains Maven!" -ForegroundColor Green
}

# Refresh environment variables in current session
$env:MAVEN_HOME = $mavenHome
$env:Path = "$env:Path;$mavenBin"

Write-Host ""
Write-Host "Maven installation completed!" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANT: Please restart your terminal/PowerShell for changes to take effect!" -ForegroundColor Yellow
Write-Host ""
Write-Host "After restart, verify installation:" -ForegroundColor Cyan
Write-Host "  mvn --version" -ForegroundColor White
Write-Host ""

# Clean up
Remove-Item $downloadPath -Force -ErrorAction SilentlyContinue

Write-Host "Installation complete! Restart your terminal and run 'mvn --version' to verify." -ForegroundColor Green
