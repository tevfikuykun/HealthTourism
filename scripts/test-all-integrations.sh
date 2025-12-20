#!/bin/bash

# Comprehensive Integration Test Script
# Tests all integrations: Vault, Flink, GraphQL, Hibernate, Tracing, Camel, etc.

echo "🧪 Starting Comprehensive Integration Tests..."
echo "================================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test results
PASSED=0
FAILED=0
SKIPPED=0

# Function to test service
test_service() {
    local name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo -n "Testing $name... "
    
    if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "$expected_status"; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAILED${NC}"
        ((FAILED++))
        return 1
    fi
}

# Function to test with POST
test_post() {
    local name=$1
    local url=$2
    local data=$3
    local expected_status=${4:-200}
    
    echo -n "Testing $name... "
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$url" \
        -H "Content-Type: application/json" \
        -d "$data")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAILED (HTTP $http_code)${NC}"
        ((FAILED++))
        return 1
    fi
}

echo "1. Testing Infrastructure Services..."
echo "--------------------------------------"

# Docker services
test_service "Zipkin" "http://localhost:9411"
test_service "Redis" "http://localhost:6379" || echo -e "${YELLOW}⚠ Redis doesn't have HTTP endpoint${NC}"
test_service "Vault" "http://localhost:8200/v1/sys/health"
test_service "Flink JobManager" "http://localhost:8081"
test_service "Neo4j" "http://localhost:7474"
test_service "Axon Server" "http://localhost:8124/actuator/health"

echo ""
echo "2. Testing Core Services..."
echo "----------------------------"

test_service "API Gateway" "http://localhost:8080/actuator/health"
test_service "Eureka Server" "http://localhost:8761"
test_service "Auth Service" "http://localhost:8001/actuator/health"
test_service "User Service" "http://localhost:8002/actuator/health"

echo ""
echo "3. Testing Integration Services..."
echo "-----------------------------------"

test_service "Vault Integration" "http://localhost:8088/actuator/health"
test_service "Camel Integration" "http://localhost:8091/actuator/health"
test_service "GraphQL Gateway" "http://localhost:8090/actuator/health"
test_service "Data Migration" "http://localhost:8092/actuator/health"
test_service "Integration Test" "http://localhost:8093/actuator/health"

echo ""
echo "4. Testing Vault Integration..."
echo "-------------------------------"

# Test Vault secret retrieval
test_post "Vault - Get AES Key" \
    "http://localhost:8088/api/vault/keys/aes" \
    '{}' \
    200

echo ""
echo "5. Testing GraphQL Gateway..."
echo "------------------------------"

# Test GraphQL query
test_post "GraphQL - Patient Data Query" \
    "http://localhost:8090/graphql" \
    '{"query":"{ patientData(patientId: \"1\") { patientId name } }"}' \
    200

echo ""
echo "6. Testing Distributed Tracing..."
echo "-----------------------------------"

# Test trace simulation
test_post "Trace Simulation" \
    "http://localhost:8093/api/trace-simulation/hospital-ftp-to-ai" \
    '{}' \
    200

echo ""
echo "7. Testing Data Migration..."
echo "-----------------------------"

# Test migration API
test_service "Migration - Get Versions" \
    "http://localhost:8092/api/migration/versions"

echo ""
echo "8. Testing Camel Integration..."
echo "--------------------------------"

test_service "Camel - Health Check" \
    "http://localhost:8091/actuator/health"

echo ""
echo "================================================"
echo "Test Results:"
echo "  ${GREEN}Passed: $PASSED${NC}"
echo "  ${RED}Failed: $FAILED${NC}"
echo "  ${YELLOW}Skipped: $SKIPPED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}❌ Some tests failed. Please check the logs above.${NC}"
    exit 1
fi



