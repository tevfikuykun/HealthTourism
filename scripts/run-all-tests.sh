#!/bin/bash

# Comprehensive test runner for all services

set -e

echo "=========================================="
echo "Running All Tests"
echo "=========================================="

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE_DIR"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Test results
PASSED=0
FAILED=0

# Function to run tests for a service
run_service_tests() {
    local service=$1
    local service_dir="microservices/$service"
    
    if [ ! -d "$service_dir" ]; then
        echo -e "${YELLOW}Skipping $service (not found)${NC}"
        return
    fi
    
    if [ ! -f "$service_dir/pom.xml" ]; then
        echo -e "${YELLOW}Skipping $service (no pom.xml)${NC}"
        return
    fi
    
    echo -e "\n${YELLOW}Testing $service...${NC}"
    
    cd "$service_dir"
    
    if mvn clean test -DskipTests=false > test-output.log 2>&1; then
        echo -e "${GREEN}✓ $service tests passed${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ $service tests failed${NC}"
        cat test-output.log
        ((FAILED++))
    fi
    
    cd "$BASE_DIR"
}

# Run tests for all services
SERVICES=(
    "auth-service"
    "user-service"
    "hospital-service"
    "doctor-service"
    "payment-service"
    "reservation-service"
    "notification-service"
)

for service in "${SERVICES[@]}"; do
    run_service_tests "$service"
done

# Run frontend tests
echo -e "\n${YELLOW}Testing frontend...${NC}"
cd microservices/frontend

if npm test -- --coverage > test-output.log 2>&1; then
    echo -e "${GREEN}✓ Frontend tests passed${NC}"
    ((PASSED++))
else
    echo -e "${RED}✗ Frontend tests failed${NC}"
    cat test-output.log
    ((FAILED++))
fi

cd "$BASE_DIR"

# Summary
echo -e "\n=========================================="
echo "Test Summary"
echo "=========================================="
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo "=========================================="

if [ $FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi

