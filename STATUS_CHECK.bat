@echo off
cls
echo ==========================================
echo   HEALTH TOURISM - SERVICE STATUS
echo ==========================================
echo.

echo [1] Docker Containers:
docker ps --format "table {{.Names}}\t{{.Status}}" | findstr /i "mysql redis rabbitmq zipkin"
echo.

echo [2] Checking Services...
echo.

echo Eureka Server (8761):
curl -s http://localhost:8761 >nul 2>&1
if errorlevel 1 (
    echo   Status: NOT RUNNING
) else (
    echo   Status: ✓ RUNNING
)
echo.

echo API Gateway (8080):
curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo   Status: NOT RUNNING
) else (
    echo   Status: ✓ RUNNING
)
echo.

echo Auth Service (8023):
curl -s http://localhost:8023/actuator/health >nul 2>&1
if errorlevel 1 (
    echo   Status: NOT RUNNING
) else (
    echo   Status: ✓ RUNNING
)
echo.

echo Frontend (3000):
curl -s http://localhost:3000 >nul 2>&1
if errorlevel 1 (
    echo   Status: NOT RUNNING
) else (
    echo   Status: ✓ RUNNING
)
echo.

echo ==========================================
echo Quick Links:
echo   Frontend: http://localhost:3000
echo   Eureka: http://localhost:8761
echo   API Gateway: http://localhost:8080
echo ==========================================
echo.
pause

