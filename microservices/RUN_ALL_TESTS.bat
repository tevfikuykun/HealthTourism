@echo off
echo ========================================
echo Running All Service Tests
echo ========================================
echo.

set FAILED=0
set PASSED=0

REM Test Auth Service
echo [1/10] Testing Auth Service...
cd auth-service
call mvn test -q
if errorlevel 1 (
    echo [FAILED] Auth Service tests failed
    set /a FAILED+=1
) else (
    echo [PASSED] Auth Service tests passed
    set /a PASSED+=1
)
cd ..

REM Test User Service
echo [2/10] Testing User Service...
cd user-service
call mvn test -q
if errorlevel 1 (
    echo [FAILED] User Service tests failed
    set /a FAILED+=1
) else (
    echo [PASSED] User Service tests passed
    set /a PASSED+=1
)
cd ..

REM Test Hospital Service
echo [3/10] Testing Hospital Service...
cd hospital-service
call mvn test -q
if errorlevel 1 (
    echo [FAILED] Hospital Service tests failed
    set /a FAILED+=1
) else (
    echo [PASSED] Hospital Service tests passed
    set /a PASSED+=1
)
cd ..

REM Test Payment Service
echo [4/10] Testing Payment Service...
cd payment-service
call mvn test -q
if errorlevel 1 (
    echo [FAILED] Payment Service tests failed
    set /a FAILED+=1
) else (
    echo [PASSED] Payment Service tests passed
    set /a PASSED+=1
)
cd ..

REM Test Reservation Service
echo [5/10] Testing Reservation Service...
cd reservation-service
call mvn test -q
if errorlevel 1 (
    echo [FAILED] Reservation Service tests failed
    set /a FAILED+=1
) else (
    echo [PASSED] Reservation Service tests passed
    set /a PASSED+=1
)
cd ..

echo.
echo ========================================
echo Test Summary
echo ========================================
echo Passed: %PASSED%
echo Failed: %FAILED%
echo ========================================

if %FAILED% gtr 0 (
    echo.
    echo WARNING: Some tests failed!
    exit /b 1
) else (
    echo.
    echo All tests passed successfully!
    exit /b 0
)

