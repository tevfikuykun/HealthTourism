@echo off
REM ============================================================
REM Health Tourism Platform - Complete Setup and Run Script
REM ============================================================

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     Health Tourism Platform - Complete Setup                 ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

REM Check Docker
echo [1/8] Checking Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker not found. Please install Docker Desktop.
    pause
    exit /b 1
)
echo Docker OK

REM Start Infrastructure
echo.
echo [2/8] Starting Infrastructure (Redis, Kafka, MySQL, etc.)...
docker-compose up -d
timeout /t 30 /nobreak >nul

REM Check infrastructure health
echo.
echo [3/8] Checking Infrastructure Health...
docker-compose ps

REM Build microservices
echo.
echo [4/8] Building Microservices...
call mvnw clean package -DskipTests -q

REM Start Eureka Server
echo.
echo [5/8] Starting Eureka Server...
cd microservices\eureka-server
start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
cd ..\..
timeout /t 20 /nobreak >nul

REM Start Config Server
echo.
echo [6/8] Starting Config Server...
cd microservices\config-server
start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
cd ..\..
timeout /t 15 /nobreak >nul

REM Start Core Services
echo.
echo [7/8] Starting Core Services...

REM Auth Service
cd microservices\auth-service
start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
cd ..\..

REM API Gateway
cd microservices\api-gateway
start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
cd ..\..

REM User Service
cd microservices\user-service
start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
cd ..\..

timeout /t 30 /nobreak >nul

REM Start remaining services
echo.
echo [8/8] Starting Remaining Services...

for %%s in (hospital-service doctor-service reservation-service payment-service notification-service) do (
    cd microservices\%%s
    start /B cmd /c "call ..\..\mvnw spring-boot:run -q"
    cd ..\..
    timeout /t 5 /nobreak >nul
)

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    STARTUP COMPLETE                          ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo Services:
echo   - Eureka Dashboard:  http://localhost:8761
echo   - API Gateway:       http://localhost:8080
echo   - Swagger UI:        http://localhost:8080/swagger-ui.html
echo.
echo Infrastructure:
echo   - Redis:             localhost:6379
echo   - Kafka:             localhost:9092
echo   - RabbitMQ:          http://localhost:15672
echo   - Elasticsearch:     http://localhost:9200
echo.
pause

