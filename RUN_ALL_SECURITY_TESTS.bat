@echo off
echo ==========================================
echo Running All Security Tests
echo ==========================================

cd /d "%~dp0"

echo.
echo [1/4] Running Penetration Tests...
call security-tests\run-penetration-tests.bat

echo.
echo [2/4] Running Security Headers Check...
call security-tests\security-headers-check.sh

echo.
echo [3/4] Running OWASP Dependency Check...
call security-tests\owasp-dependency-check.sh

echo.
echo [4/4] Running Load Tests...
echo Note: Load tests may take a while. Press Ctrl+C to skip.
timeout /t 5 /nobreak >nul
call load-tests\run-load-tests.bat

echo.
echo ==========================================
echo All Security Tests Completed!
echo ==========================================
echo Check the results in:
echo - security-tests/owasp-reports/
echo - load-tests/results/
echo ==========================================

pause

