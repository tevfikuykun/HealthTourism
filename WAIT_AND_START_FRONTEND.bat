@echo off
echo ==========================================
echo Frontend Dependency Installation Check
echo ==========================================
echo.

cd /d "%~dp0\microservices\frontend"

echo Checking if dependencies are installed...
timeout /t 5 /nobreak >nul

if exist node_modules\react-helmet-async (
    echo ✓ Dependencies are installed!
    echo.
    echo Starting frontend...
    call npm.cmd run dev
) else (
    echo ⏳ Dependencies are still being installed...
    echo Please wait 1-2 minutes, then run this script again.
    echo.
    echo Or run manually:
    echo   cd microservices\frontend
    echo   npm run dev
    echo.
    pause
)

