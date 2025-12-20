@echo off
REM Comprehensive Integration Test Script for Windows
REM Tests all integrations: Vault, Flink, GraphQL, Hibernate, Tracing, Camel, etc.

echo 🧪 Starting Comprehensive Integration Tests...
echo ================================================
echo.

set PASSED=0
set FAILED=0

echo 1. Testing Infrastructure Services...
echo --------------------------------------

curl -s http://localhost:9411 >nul 2>&1
if errorlevel 1 (
    echo ❌ Zipkin - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Zipkin - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:8200/v1/sys/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Vault - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Vault - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:8081 >nul 2>&1
if errorlevel 1 (
    echo ❌ Flink JobManager - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Flink JobManager - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:7474 >nul 2>&1
if errorlevel 1 (
    echo ❌ Neo4j - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Neo4j - PASSED
    set /a PASSED+=1
)

echo.
echo 2. Testing Core Services...
echo ----------------------------

curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ API Gateway - FAILED
    set /a FAILED+=1
) else (
    echo ✅ API Gateway - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:8761 >nul 2>&1
if errorlevel 1 (
    echo ❌ Eureka Server - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Eureka Server - PASSED
    set /a PASSED+=1
)

echo.
echo 3. Testing Integration Services...
echo -----------------------------------

curl -s http://localhost:8088/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Vault Integration - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Vault Integration - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:8091/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Camel Integration - FAILED
    set /a FAILED+=1
) else (
    echo ✅ Camel Integration - PASSED
    set /a PASSED+=1
)

curl -s http://localhost:8090/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ GraphQL Gateway - FAILED
    set /a FAILED+=1
) else (
    echo ✅ GraphQL Gateway - PASSED
    set /a PASSED+=1
)

echo.
echo ================================================
echo Test Results:
echo   Passed: %PASSED%
echo   Failed: %FAILED%
echo.

if %FAILED%==0 (
    echo ✅ All tests passed!
    exit /b 0
) else (
    echo ❌ Some tests failed. Please check the logs above.
    exit /b 1
)



