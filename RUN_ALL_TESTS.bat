@echo off
echo ============================================
echo Running All Tests - Health Tourism Project
echo ============================================
echo.

echo [1/5] Running Auth Service Tests...
cd microservices\auth-service
call mvn clean test
if %errorlevel% neq 0 (
    echo Auth Service tests failed!
    pause
    exit /b %errorlevel%
)
cd ..\..

echo.
echo [2/5] Running User Service Tests...
cd microservices\user-service
if exist pom.xml (
    call mvn clean test -DskipTests=false
)
cd ..\..

echo.
echo [3/5] Running Hospital Service Tests...
cd microservices\hospital-service
if exist pom.xml (
    call mvn clean test -DskipTests=false
)
cd ..\..

echo.
echo [4/5] Generating Test Coverage Report...
cd microservices\auth-service
call mvn jacoco:report
echo Coverage report generated at: microservices\auth-service\target\site\jacoco\index.html
cd ..\..

echo.
echo [5/5] Running Integration Tests...
echo Integration tests can be run manually for each service.
echo.

echo ============================================
echo Test Execution Complete!
echo ============================================
echo.
echo Test Results:
echo - Auth Service: See output above
echo - Other Services: Add tests using Auth Service as template
echo.
echo Next Steps:
echo 1. Add tests for other services
echo 2. Set up CI/CD pipeline
echo 3. Configure monitoring
echo ============================================
pause

