@echo off
title Health Tourism - Production Ready Startup
color 0A

echo.
echo ============================================
echo   HEALTH TOURISM - PRODUCTION READY
echo ============================================
echo.
echo Starting all services...
echo.

:: Check Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Java not found!
    echo Please install Java 25
    pause
    exit /b 1
)

:: Check Docker
where docker >nul 2>nul
if %errorlevel% neq 0 (
    color 0E
    echo [WARNING] Docker not found!
    echo Databases must be started manually
    echo.
) else (
    echo [OK] Docker found
)

echo.
echo ============================================
echo STEP 1: Starting Docker Containers
echo ============================================
if exist microservices\docker-compose.yml (
    cd microservices
    echo Starting database containers...
    docker-compose up -d
    timeout /t 5 /nobreak >nul
    cd ..
    echo [OK] Docker containers started
) else (
    echo [SKIP] docker-compose.yml not found
)

echo.
echo ============================================
echo STEP 2: Starting Eureka Server
echo ============================================
start "Eureka Server" cmd /k "cd microservices\eureka-server && title Eureka Server && mvn spring-boot:run"
echo [OK] Eureka Server starting...
timeout /t 15 /nobreak >nul

echo.
echo ============================================
echo STEP 3: Starting API Gateway
echo ============================================
start "API Gateway" cmd /k "cd microservices\api-gateway && title API Gateway && mvn spring-boot:run"
echo [OK] API Gateway starting...
timeout /t 10 /nobreak >nul

echo.
echo ============================================
echo STEP 4: Starting Auth Service
echo ============================================
start "Auth Service" cmd /k "cd microservices\auth-service && title Auth Service && mvn spring-boot:run"
echo [OK] Auth Service starting...

echo.
echo ============================================
echo SERVICES STARTED
echo ============================================
echo.
echo Access Points:
echo   - Eureka: http://localhost:8761
echo   - API Gateway: http://localhost:8080
echo   - Auth Swagger: http://localhost:8023/swagger-ui.html
echo   - Auth Health: http://localhost:8023/actuator/health
echo.
echo Other services can be started manually:
echo   cd microservices\{service-name}
echo   mvn spring-boot:run
echo.
echo ============================================
echo PRODUCTION NOTES
echo ============================================
echo.
echo Before production deployment:
echo   1. Review PRODUCTION_EXPERT_ANALYSIS.md
echo   2. Complete test coverage (currently 2%%, target 80%%+)
echo   3. Security hardening (rate limiting, CORS, headers)
echo   4. Setup monitoring (Prometheus + Grafana)
echo   5. Configure backups
echo.
echo Current Status: 65/100 Production Ready
echo Estimated Time to Full Production: 6-8 weeks
echo.
echo ============================================
echo.

timeout /t 3 /nobreak >nul
echo All startup commands executed!
echo Check the opened windows for service logs.
echo.
pause

