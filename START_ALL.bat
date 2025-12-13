@echo off
title Health Tourism - Starting All Services
color 0A

echo.
echo ==========================================
echo   HEALTH TOURISM - COMPLETE STARTUP
echo ==========================================
echo.

cd /d "%~dp0"

:: Step 1: Start Docker
echo [1/6] Starting Docker containers...
cd microservices
start /min cmd /c "docker-compose up -d"
echo Waiting for Docker containers to start (35 seconds)...
timeout /t 35 /nobreak >nul
cd ..
echo ✓ Docker containers started
echo.

:: Step 2: Start Eureka Server
echo [2/6] Starting Eureka Server...
cd microservices\eureka-server
start "Eureka Server" cmd /k "title Eureka Server - Port 8761 && mvn spring-boot:run"
cd ..\..
echo Waiting for Eureka Server (15 seconds)...
timeout /t 15 /nobreak >nul
echo ✓ Eureka Server started
echo.

:: Step 3: Start API Gateway
echo [3/6] Starting API Gateway...
cd microservices\api-gateway
start "API Gateway" cmd /k "title API Gateway - Port 8080 && mvn spring-boot:run"
cd ..\..
echo Waiting for API Gateway (10 seconds)...
timeout /t 10 /nobreak >nul
echo ✓ API Gateway started
echo.

:: Step 4: Start Auth Service
echo [4/6] Starting Auth Service...
cd microservices\auth-service
start "Auth Service" cmd /k "title Auth Service - Port 8023 && mvn spring-boot:run"
cd ..\..
timeout /t 5 /nobreak >nul
echo ✓ Auth Service started
echo.

:: Step 5: Start Core Services
echo [5/6] Starting Core Services...
cd microservices

start "User Service" cmd /k "title User Service - Port 8001 && cd user-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Hospital Service" cmd /k "title Hospital Service - Port 8002 && cd hospital-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Doctor Service" cmd /k "title Doctor Service - Port 8003 && cd doctor-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Payment Service" cmd /k "title Payment Service - Port 8010 && cd payment-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

cd ..
echo ✓ Core services started
echo.

:: Step 6: Start Frontend
echo [6/6] Starting Frontend...
cd microservices\frontend
start "Frontend" cmd /k "title Frontend - Port 3000 && npm.cmd run dev"
cd ..\..
echo ✓ Frontend started
echo.

:: Final wait
echo ==========================================
echo Waiting for services to initialize...
echo ==========================================
timeout /t 20 /nobreak >nul

:: Display info
cls
echo.
echo ==========================================
echo   ✓ ALL SERVICES STARTED SUCCESSFULLY!
echo ==========================================
echo.
echo Access Points:
echo   🌐 Frontend:        http://localhost:3000
echo   🚪 API Gateway:     http://localhost:8080
echo   📊 Eureka:          http://localhost:8761
echo   📖 Swagger (Auth):  http://localhost:8023/swagger-ui.html
echo   🐰 RabbitMQ:        http://localhost:15672 (admin/admin)
echo.
echo Services Running:
echo   ✓ Docker containers (MySQL, Redis, RabbitMQ)
echo   ✓ Eureka Server (8761)
echo   ✓ API Gateway (8080)
echo   ✓ Auth Service (8023)
echo   ✓ User Service (8001)
echo   ✓ Hospital Service (8002)
echo   ✓ Doctor Service (8003)
echo   ✓ Payment Service (8010)
echo   ✓ Frontend (3000)
echo.
echo Note: Services may take 1-2 minutes to fully initialize.
echo Check Eureka Dashboard to see all registered services.
echo.
echo Press any key to open services in browser...
pause >nul

:: Open browsers
start http://localhost:3000
timeout /t 2 /nobreak >nul
start http://localhost:8761
timeout /t 2 /nobreak >nul
start http://localhost:8080

echo.
echo ==========================================
echo   Services opened in browser!
echo ==========================================
echo.
echo To stop services:
echo   1. Close all command windows
echo   2. Run: cd microservices ^&^& docker-compose down
echo.
pause

