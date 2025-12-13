@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo Health Tourism - Complete Setup and Run
echo ==========================================
echo.

cd /d "%~dp0"

:: Check if Docker is running
echo [1/5] Checking Docker...
docker ps >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running! Please start Docker Desktop.
    pause
    exit /b 1
)
echo OK: Docker is running
echo.

:: Check if Java is installed
echo [2/5] Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed! Please install Java 25.
    pause
    exit /b 1
)
echo OK: Java is installed
echo.

:: Check if Maven is installed
echo [3/5] Checking Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed! Please install Maven.
    pause
    exit /b 1
)
echo OK: Maven is installed
echo.

:: Check if Node.js is installed
echo [4/5] Checking Node.js...
node --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Node.js is not installed! Please install Node.js 20+.
    pause
    exit /b 1
)
echo OK: Node.js is installed
echo.

:: Start Docker containers (databases, Redis, RabbitMQ)
echo [5/5] Starting Docker containers...
cd microservices
docker-compose up -d
if errorlevel 1 (
    echo ERROR: Failed to start Docker containers!
    pause
    exit /b 1
)
echo OK: Docker containers started
echo Waiting for databases to be ready...
timeout /t 30 /nobreak >nul
cd ..
echo.

:: Install Frontend dependencies
echo ==========================================
echo Installing Frontend Dependencies...
echo ==========================================
cd microservices\frontend
if not exist node_modules (
    echo Installing npm packages...
    call npm install
    if errorlevel 1 (
        echo ERROR: Failed to install frontend dependencies!
        pause
        exit /b 1
    )
    echo OK: Frontend dependencies installed
) else (
    echo Frontend dependencies already installed
)
cd ..\..
echo.

:: Create logs directory
if not exist microservices\logs mkdir microservices\logs

:: Start Eureka Server first
echo ==========================================
echo Starting Eureka Server...
echo ==========================================
cd microservices\eureka-server
start "Eureka Server" cmd /k "title Eureka Server && mvn spring-boot:run"
cd ..\..
echo Waiting for Eureka Server to start...
timeout /t 15 /nobreak >nul
echo.

:: Start API Gateway
echo ==========================================
echo Starting API Gateway...
echo ==========================================
cd microservices\api-gateway
start "API Gateway" cmd /k "title API Gateway && mvn spring-boot:run"
cd ..\..
echo Waiting for API Gateway to start...
timeout /t 10 /nobreak >nul
echo.

:: Start Core Services
echo ==========================================
echo Starting Core Services...
echo ==========================================
cd microservices

start "Auth Service" cmd /k "title Auth Service && cd auth-service && mvn spring-boot:run"
timeout /t 3 /nobreak >nul

start "User Service" cmd /k "title User Service && cd user-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Hospital Service" cmd /k "title Hospital Service && cd hospital-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Doctor Service" cmd /k "title Doctor Service && cd doctor-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Payment Service" cmd /k "title Payment Service && cd payment-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

start "Reservation Service" cmd /k "title Reservation Service && cd reservation-service && mvn spring-boot:run"
timeout /t 2 /nobreak >nul

cd ..
echo.

:: Start Frontend
echo ==========================================
echo Starting Frontend...
echo ==========================================
cd microservices\frontend
start "Frontend" cmd /k "title Frontend && npm run dev"
cd ..\..
echo.

:: Wait a bit for services to start
echo ==========================================
echo Waiting for services to initialize...
echo ==========================================
timeout /t 20 /nobreak >nul

:: Display status
echo.
echo ==========================================
echo Setup Complete! Services are starting...
echo ==========================================
echo.
echo Access Points:
echo - Frontend: http://localhost:3000
echo - API Gateway: http://localhost:8080
echo - Eureka Dashboard: http://localhost:8761
echo - Swagger UI (Auth): http://localhost:8023/swagger-ui.html
echo - RabbitMQ Management: http://localhost:15672 (admin/admin)
echo - Redis: localhost:6379
echo.
echo Services Status:
echo - Docker containers: Running
echo - Eureka Server: Starting...
echo - API Gateway: Starting...
echo - Core Services: Starting...
echo - Frontend: Starting...
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
echo 1. Close all command windows
echo 2. Run: docker-compose down (in microservices folder)
echo.
pause

