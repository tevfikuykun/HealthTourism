@echo off
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     Health Tourism Platform - Stop All Services              ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

cd /d %~dp0

echo Stopping Java processes...
taskkill /F /IM java.exe 2>nul
if errorlevel 1 (
    echo No Java processes found
) else (
    echo Java processes stopped
)

echo.
echo Stopping Docker containers...
docker-compose down

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    ALL SERVICES STOPPED                      ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
pause








