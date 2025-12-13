@echo off
echo ==========================================
echo Health Tourism - Complete Project Setup
echo ==========================================
echo.

cd /d "%~dp0"

:: Step 1: Start Docker containers
echo [Step 1/5] Starting Docker containers (databases, Redis, RabbitMQ)...
cd microservices
docker-compose up -d
if errorlevel 1 (
    echo ERROR: Failed to start Docker containers!
    echo Please make sure Docker Desktop is running.
    pause
    exit /b 1
)
echo OK: Docker containers started
echo Waiting 30 seconds for databases to initialize...
timeout /t 30 /nobreak >nul
cd ..
echo.

:: Step 2: Check Frontend dependencies
echo [Step 2/5] Checking Frontend dependencies...
cd microservices\frontend
if not exist node_modules (
    echo Installing npm packages (this may take a few minutes)...
    call npm.cmd install
    if errorlevel 1 (
        echo ERROR: Failed to install frontend dependencies!
        pause
        exit /b 1
    )
    echo OK: Frontend dependencies installed
) else (
    echo OK: Frontend dependencies already installed
)
cd ..\..
echo.

:: Step 3: Start Eureka Server
echo [Step 3/5] Starting Eureka Server...
cd microservices\eureka-server
start "Eureka Server" cmd /k "title Eureka Server && mvn spring-boot:run"
cd ..\..
echo Waiting 15 seconds for Eureka Server to start...
timeout /t 15 /nobreak >nul
echo.

:: Step 4: Start API Gateway
echo [Step 4/5] Starting API Gateway...
cd microservices\api-gateway
start "API Gateway" cmd /k "title API Gateway && mvn spring-boot:run"
cd ..\..
echo Waiting 10 seconds for API Gateway to start...
timeout /t 10 /nobreak >nul
echo.

:: Step 5: Start Core Services
echo [Step 5/5] Starting Core Microservices...
cd microservices

echo Starting Auth Service...
start "Auth Service" cmd /k "title Auth Service && cd auth-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

echo Starting User Service...
start "User Service" cmd /k "title User Service && cd user-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Hospital Service...
start "Hospital Service" cmd /k "title Hospital Service && cd hospital-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Doctor Service...
start "Doctor Service" cmd /k "title Doctor Service && cd doctor-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Payment Service...
start "Payment Service" cmd /k "title Payment Service && cd payment-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

echo Starting Reservation Service...
start "Reservation Service" cmd /k "title Reservation Service && cd reservation-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

cd ..
echo.

:: Step 6: Start Frontend
echo Starting Frontend...
cd microservices\frontend
start "Frontend" cmd /k "title Frontend && npm.cmd run dev"
cd ..\..
echo.

:: Final wait
echo ==========================================
echo Waiting for all services to initialize...
echo ==========================================
timeout /t 20 /nobreak >nul

:: Display information
echo.
echo ==========================================
echo Setup Complete! Services are running...
echo ==========================================
echo.
echo Access Points:
echo   Frontend:        http://localhost:3000
echo   API Gateway:     http://localhost:8080
echo   Eureka Dashboard: http://localhost:8761
echo   Swagger (Auth):  http://localhost:8023/swagger-ui.html
echo   RabbitMQ:        http://localhost:15672 (admin/admin)
echo.
echo Services Status:
echo   - Docker containers: Running
echo   - Eureka Server: Starting (check port 8761)
echo   - API Gateway: Starting (check port 8080)
echo   - Core Services: Starting (check Eureka dashboard)
echo   - Frontend: Starting (check port 3000)
echo.
echo Note: Services may take 1-2 minutes to fully start.
echo Check Eureka Dashboard to see registered services.
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
echo All services are running!
echo ==========================================
echo.
echo To stop services:
echo   1. Close all command windows
echo   2. Run: docker-compose down (in microservices folder)
echo.
echo To check service status:
echo   Run: microservices\check-services-status.bat
echo.
pause

