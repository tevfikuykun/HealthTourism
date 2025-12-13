@echo off
echo ==========================================
echo Checking Service Status
echo ==========================================
echo.

echo Checking Docker containers...
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | findstr "mysql redis rabbitmq"
echo.

echo Checking Eureka Server...
curl -s http://localhost:8761 >nul 2>&1
if errorlevel 1 (
    echo Eureka Server: NOT RUNNING
) else (
    echo Eureka Server: RUNNING
)
echo.

echo Checking API Gateway...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo API Gateway: NOT RUNNING
) else (
    echo API Gateway: RUNNING
)
echo.

echo Checking Auth Service...
curl -s http://localhost:8023/actuator/health >nul 2>&1
if errorlevel 1 (
    echo Auth Service: NOT RUNNING
) else (
    echo Auth Service: RUNNING
)
echo.

echo Checking Frontend...
curl -s http://localhost:3000 >nul 2>&1
if errorlevel 1 (
    echo Frontend: NOT RUNNING
) else (
    echo Frontend: RUNNING
)
echo.

echo ==========================================
echo Status Check Complete
echo ==========================================
pause

