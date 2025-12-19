@echo off
REM Maven Installation Script for Windows
REM This script downloads and installs Apache Maven

echo 🔧 Maven Installation Script
echo ============================
echo.

REM Check if Maven is already installed
mvn --version >nul 2>&1
if %errorlevel% == 0 (
    echo ✅ Maven is already installed!
    mvn --version
    exit /b 0
)

echo 📥 Downloading Apache Maven...
echo.

REM Maven version and paths
set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set DOWNLOAD_PATH=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip
set INSTALL_PATH=C:\Program Files\Apache

REM Create install directory
if not exist "%INSTALL_PATH%" mkdir "%INSTALL_PATH%"

REM Download Maven using PowerShell
echo Downloading Maven...
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%DOWNLOAD_PATH%' -UseBasicParsing"

if not exist "%DOWNLOAD_PATH%" (
    echo ❌ Download failed!
    echo.
    echo Please download manually from:
    echo https://maven.apache.org/download.cgi
    echo.
    echo Then extract to: %INSTALL_PATH%
    pause
    exit /b 1
)

echo ✅ Download completed!
echo.

REM Extract Maven
echo 📦 Extracting Maven...
powershell -Command "Expand-Archive -Path '%DOWNLOAD_PATH%' -DestinationPath '%INSTALL_PATH%' -Force"

if not exist "%INSTALL_PATH%\apache-maven-%MAVEN_VERSION%" (
    echo ❌ Extraction failed!
    pause
    exit /b 1
)

echo ✅ Extraction completed!
echo.

REM Set Environment Variables
echo ⚙️ Setting environment variables...

set MAVEN_HOME=%INSTALL_PATH%\apache-maven-%MAVEN_VERSION%

REM Set MAVEN_HOME
setx MAVEN_HOME "%MAVEN_HOME%" >nul

REM Add to PATH
for /f "tokens=2*" %%a in ('reg query "HKCU\Environment" /v Path 2^>nul') do set "CURRENT_PATH=%%b"
echo %CURRENT_PATH% | findstr /C:"%MAVEN_HOME%\bin" >nul
if %errorlevel% neq 0 (
    setx Path "%CURRENT_PATH%;%MAVEN_HOME%\bin" >nul
    echo ✅ PATH updated!
) else (
    echo ✅ PATH already contains Maven!
)

echo.
echo ✅ Maven installation completed!
echo.
echo ⚠️  IMPORTANT: Please restart your terminal for changes to take effect!
echo.
echo After restart, verify installation:
echo   mvn --version
echo.

REM Clean up
del "%DOWNLOAD_PATH%" >nul 2>&1

echo 🎉 Installation complete! Restart your terminal and run 'mvn --version' to verify.
pause
