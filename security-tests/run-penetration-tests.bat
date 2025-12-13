@echo off
echo ==========================================
echo Running Penetration Test Suite
echo ==========================================

cd /d "%~dp0\.."

echo Running SQL Injection Tests...
call mvn test -Dtest=PenetrationTestSuite#testSqlInjection* -pl microservices\auth-service

echo Running XSS Tests...
call mvn test -Dtest=PenetrationTestSuite#testXss* -pl microservices\auth-service

echo Running Authentication Bypass Tests...
call mvn test -Dtest=PenetrationTestSuite#testAuthBypass* -pl microservices\auth-service

echo Running Authorization Tests...
call mvn test -Dtest=PenetrationTestSuite#testAuthorization* -pl microservices\auth-service

echo Running Rate Limiting Tests...
call mvn test -Dtest=PenetrationTestSuite#testRateLimiting -pl microservices\auth-service

echo Running Input Validation Tests...
call mvn test -Dtest=PenetrationTestSuite#test*Validation -pl microservices\auth-service

echo Penetration tests completed!

