@echo off
echo Installing all frontend dependencies...
echo This may take 2-3 minutes...
echo.

cd /d "%~dp0"

call npm.cmd install

echo.
echo ==========================================
echo Dependencies installed!
echo ==========================================
echo.
echo You can now run: npm run dev
echo.
pause

