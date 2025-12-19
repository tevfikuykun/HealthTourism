@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║     Health Tourism Platform - Complete System Startup        ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

cd /d %~dp0

REM ============================================================
REM STEP 1: Check Docker
REM ============================================================
echo [STEP 1/6] Checking Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)
echo [OK] Docker is running

REM ============================================================
REM STEP 2: Start Infrastructure
REM ============================================================
echo.
echo [STEP 2/6] Starting Infrastructure Services...
echo   - MySQL, PostgreSQL, Redis
echo   - Kafka, RabbitMQ, Mosquitto
echo   - Elasticsearch, Milvus
echo.

docker-compose up -d

echo Waiting for infrastructure to be ready (60 seconds)...
timeout /t 60 /nobreak >nul

REM ============================================================
REM STEP 3: Verify Infrastructure
REM ============================================================
echo.
echo [STEP 3/6] Verifying Infrastructure...
docker-compose ps

REM Check Redis
docker exec healthtourism-redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo [WARN] Redis not responding yet
) else (
    echo [OK] Redis is ready
)

REM ============================================================
REM STEP 4: Build Services (if needed)
REM ============================================================
echo.
echo [STEP 4/6] Building Services...
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests -q -T 4
) else (
    echo [WARN] Maven wrapper not found, skipping build
)

REM ============================================================
REM STEP 5: Start Core Services
REM ============================================================
echo.
echo [STEP 5/6] Starting Core Services...

REM Start Eureka Server
echo Starting Eureka Server (port 8761)...
cd microservices\eureka-server
start "Eureka Server" cmd /c "call ..\..\mvnw.cmd spring-boot:run"
cd ..\..
timeout /t 25 /nobreak >nul

REM Start Config Server
echo Starting Config Server (port 8888)...
cd microservices\config-server
start "Config Server" cmd /c "call ..\..\mvnw.cmd spring-boot:run"
cd ..\..
timeout /t 15 /nobreak >nul

REM Start Auth Service
echo Starting Auth Service (port 8023)...
cd microservices\auth-service
start "Auth Service" cmd /c "call ..\..\mvnw.cmd spring-boot:run"
cd ..\..
timeout /t 10 /nobreak >nul

REM Start API Gateway
echo Starting API Gateway (port 8080)...
cd microservices\api-gateway
start "API Gateway" cmd /c "call ..\..\mvnw.cmd spring-boot:run"
cd ..\..
timeout /t 10 /nobreak >nul

REM ============================================================
REM STEP 6: Start Additional Services
REM ============================================================
echo.
echo [STEP 6/6] Starting Additional Services...

set SERVICES=user-service hospital-service doctor-service reservation-service payment-service notification-service

for %%s in (%SERVICES%) do (
    if exist "microservices\%%s\pom.xml" (
        echo Starting %%s...
        cd microservices\%%s
        start "%%s" cmd /c "call ..\..\mvnw.cmd spring-boot:run"
        cd ..\..
        timeout /t 5 /nobreak >nul
    )
)

REM ============================================================
REM COMPLETE
REM ============================================================
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    SYSTEM STARTUP COMPLETE                   ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ APPLICATION ENDPOINTS                                       │
echo ├─────────────────────────────────────────────────────────────┤
echo │ Eureka Dashboard:    http://localhost:8761                  │
echo │ API Gateway:         http://localhost:8080                  │
echo │ Swagger UI:          http://localhost:8080/swagger-ui.html  │
echo │ Auth Service:        http://localhost:8023                  │
echo └─────────────────────────────────────────────────────────────┘
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ INFRASTRUCTURE                                              │
echo ├─────────────────────────────────────────────────────────────┤
echo │ Redis:               localhost:6379                         │
echo │ MySQL:               localhost:3306                         │
echo │ PostgreSQL:          localhost:5432                         │
echo │ Kafka:               localhost:9092                         │
echo │ RabbitMQ Console:    http://localhost:15672 (guest/guest)   │
echo │ Elasticsearch:       http://localhost:9200                  │
echo │ Milvus:              localhost:19530                        │
echo │ MQTT:                localhost:1883                         │
echo └─────────────────────────────────────────────────────────────┘
echo.
echo Press any key to open Eureka Dashboard...
pause >nul
start http://localhost:8761




