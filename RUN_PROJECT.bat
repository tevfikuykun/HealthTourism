@echo off
echo ============================================
echo Health Tourism Project - Complete Startup
echo ============================================
echo.

echo Checking prerequisites...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 25
    pause
    exit /b 1
)

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo WARNING: Docker not found! Database containers won't start
    echo You can start databases manually
    pause
)

echo.
echo ============================================
echo Step 1: Starting Docker Containers
echo ============================================
cd microservices
if exist docker-compose.yml (
    echo Starting all database containers...
    docker-compose up -d
    timeout /t 5 /nobreak >nul
    echo Docker containers started!
) else (
    echo docker-compose.yml not found!
)
cd ..

echo.
echo ============================================
echo Step 2: Building Common Modules
echo ============================================
if exist microservices\common-exception-handler (
    echo Building common-exception-handler...
    cd microservices\common-exception-handler
    call mvn clean install -DskipTests
    cd ..\..
)

echo.
echo ============================================
echo Step 3: Starting Eureka Server
echo ============================================
echo Starting Eureka Server in new window...
start "Eureka Server" cmd /k "cd microservices\eureka-server && echo Starting Eureka Server... && mvn spring-boot:run"
timeout /t 15 /nobreak >nul
echo Eureka Server starting (waiting 15 seconds for startup)...

echo.
echo ============================================
echo Step 4: Starting API Gateway
echo ============================================
echo Starting API Gateway in new window...
start "API Gateway" cmd /k "cd microservices\api-gateway && echo Starting API Gateway... && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ============================================
echo Step 5: Starting Services
echo ============================================
echo Services can be started manually or use start-services.bat
echo.
echo Quick start commands:
echo   - Auth Service: cd microservices\auth-service ^&^& mvn spring-boot:run
echo   - User Service: cd microservices\user-service ^&^& mvn spring-boot:run
echo.
echo Or run all services:
echo   cd microservices
echo   start-services.bat

echo.
echo ============================================
echo Access Points
echo ============================================
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Auth Service Swagger: http://localhost:8023/swagger-ui.html
echo - Auth Service Health: http://localhost:8023/actuator/health
echo.
echo ============================================
echo IMPORTANT NOTES
echo ============================================
echo 1. Make sure all databases are running (docker-compose up -d)
echo 2. Wait for Eureka Server to be ready before starting services
echo 3. Check PRODUCTION_EXPERT_ANALYSIS.md for missing features
echo 4. Run tests: RUN_ALL_TESTS.bat
echo ============================================
echo.
pause

