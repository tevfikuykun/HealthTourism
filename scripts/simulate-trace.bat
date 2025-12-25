@echo off
REM Trace Simulation Script for Windows
REM Simulates "Hospital FTP -> AI Processing" flow and shows trace in Zipkin

echo 🔍 Starting Trace Simulation...
echo.

REM Check if Zipkin is running
curl -s http://localhost:9411 >nul 2>&1
if errorlevel 1 (
    echo ❌ Zipkin is not running. Please start it first:
    echo    docker-compose up -d zipkin
    exit /b 1
)

REM Check if Integration Test Service is running
curl -s http://localhost:8093/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Integration Test Service is not running. Please start it first:
    echo    cd microservices\integration-test-service ^&^& mvn spring-boot:run
    exit /b 1
)

echo ✅ Services are running
echo.
echo 🚀 Triggering trace simulation...
echo.

REM Trigger trace
curl -X POST http://localhost:8093/api/trace-simulation/hospital-ftp-to-ai

echo.
echo.
echo ✅ Trace created successfully!
echo.
echo 🔍 View trace in Zipkin:
echo    http://localhost:9411
echo.
echo    Search for: hospital-ftp-to-ai-flow
echo.








