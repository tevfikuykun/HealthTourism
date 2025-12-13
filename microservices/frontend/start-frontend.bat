@echo off
echo Starting Frontend...
echo.

cd /d "%~dp0"

echo Checking dependencies...
if not exist node_modules (
    echo Installing dependencies (this may take 2-3 minutes)...
    call npm.cmd install --legacy-peer-deps
    echo.
)

echo Starting development server...
call npm.cmd run dev

