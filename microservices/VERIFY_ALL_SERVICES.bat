@echo off
echo ========================================
echo Verifying All Services
echo ========================================
echo.

set SERVICES_OK=0
set SERVICES_FAILED=0

echo Checking service directories and key files...
echo.

REM Check critical services
set SERVICES=auth-service user-service hospital-service payment-service reservation-service api-gateway eureka-server

for %%S in (%SERVICES%) do (
    echo Checking %%S...
    if exist "%%S\pom.xml" (
        if exist "%%S\src\main\java" (
            echo [OK] %%S structure is valid
            set /a SERVICES_OK+=1
        ) else (
            echo [FAIL] %%S missing src directory
            set /a SERVICES_FAILED+=1
        )
    ) else (
        echo [FAIL] %%S missing pom.xml
        set /a SERVICES_FAILED+=1
    )
)

echo.
echo ========================================
echo Verification Summary
echo ========================================
echo Services OK: %SERVICES_OK%
echo Services Failed: %SERVICES_FAILED%
echo ========================================

if %SERVICES_FAILED% gtr 0 (
    echo.
    echo WARNING: Some services have issues!
    exit /b 1
) else (
    echo.
    echo All critical services verified!
    exit /b 0
)

