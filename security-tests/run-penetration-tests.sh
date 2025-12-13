#!/bin/bash

echo "=========================================="
echo "Running Penetration Test Suite"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Navigate to project root
cd "$(dirname "$0")/.."

# Run penetration tests
echo -e "${YELLOW}Running SQL Injection Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#testSqlInjection* -pl microservices/auth-service

echo -e "${YELLOW}Running XSS Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#testXss* -pl microservices/auth-service

echo -e "${YELLOW}Running Authentication Bypass Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#testAuthBypass* -pl microservices/auth-service

echo -e "${YELLOW}Running Authorization Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#testAuthorization* -pl microservices/auth-service

echo -e "${YELLOW}Running Rate Limiting Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#testRateLimiting -pl microservices/auth-service

echo -e "${YELLOW}Running Input Validation Tests...${NC}"
mvn test -Dtest=PenetrationTestSuite#test*Validation -pl microservices/auth-service

echo -e "${GREEN}Penetration tests completed!${NC}"

