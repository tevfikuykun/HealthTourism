#!/bin/bash

# Complete Test Suite Runner
# Executes all 4 test levels: Unit/Mutation, Contract, Performance, Security

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Health Tourism Platform - Complete Test Suite            ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"

REPORT_DIR="$(pwd)/test-reports"
mkdir -p "$REPORT_DIR"

# Track test results
UNIT_RESULT=0
CONTRACT_RESULT=0
PERFORMANCE_RESULT=0
SECURITY_RESULT=0

# ============================================
# 1. UNIT & MUTATION TESTS (JUnit 5 + PITest)
# ============================================
echo -e "\n${YELLOW}═══ Stage 1: Unit & Mutation Tests ═══${NC}"

cd microservices/auth-service
echo "Running unit tests with JaCoCo coverage..."
./mvnw clean test jacoco:report -q || UNIT_RESULT=1

echo "Running mutation tests with PITest..."
./mvnw org.pitest:pitest-maven:mutationCoverage -q || UNIT_RESULT=1

# Copy reports
cp -r target/site/jacoco "$REPORT_DIR/unit-coverage" 2>/dev/null || true
cp -r target/pit-reports "$REPORT_DIR/mutation-coverage" 2>/dev/null || true

cd ../..

if [ $UNIT_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ Unit & Mutation tests passed${NC}"
else
    echo -e "${RED}❌ Unit & Mutation tests failed${NC}"
fi

# ============================================
# 2. CONTRACT TESTS (Pact.io)
# ============================================
echo -e "\n${YELLOW}═══ Stage 2: Contract Tests ═══${NC}"

cd contract-tests/auth-service-consumer
echo "Running Pact consumer tests..."
mvn clean test -q || CONTRACT_RESULT=1

# Publish pacts to broker (if available)
if [ -n "$PACT_BROKER_URL" ]; then
    echo "Publishing pacts to broker..."
    mvn pact:publish -q || true
fi

# Copy reports
cp -r target/pacts "$REPORT_DIR/contract-pacts" 2>/dev/null || true

cd ../..

if [ $CONTRACT_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ Contract tests passed${NC}"
else
    echo -e "${RED}❌ Contract tests failed${NC}"
fi

# ============================================
# 3. PERFORMANCE TESTS (k6)
# ============================================
echo -e "\n${YELLOW}═══ Stage 3: Performance Tests ═══${NC}"

# Check if k6 is installed
if command -v k6 &> /dev/null; then
    echo "Running k6 load tests..."
    
    # Smoke test only for CI (full test takes too long)
    k6 run --out json="$REPORT_DIR/k6-results.json" \
        -e BASE_URL=http://localhost:8080 \
        --duration 1m \
        --vus 10 \
        load-tests/k6/health-tourism-load-test.js || PERFORMANCE_RESULT=1
    
    # Run bottleneck analysis
    k6 run --out json="$REPORT_DIR/bottleneck-results.json" \
        -e BASE_URL=http://localhost:8080 \
        --duration 2m \
        load-tests/k6/bottleneck-analysis.js || true
else
    echo -e "${YELLOW}k6 not installed. Running with Docker...${NC}"
    
    docker run --rm \
        -v "$(pwd)/load-tests/k6:/scripts" \
        -v "$REPORT_DIR:/reports" \
        --network host \
        grafana/k6 run \
        --out json=/reports/k6-results.json \
        -e BASE_URL=http://localhost:8080 \
        --duration 1m \
        --vus 10 \
        /scripts/health-tourism-load-test.js || PERFORMANCE_RESULT=1
fi

if [ $PERFORMANCE_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ Performance tests passed${NC}"
else
    echo -e "${RED}❌ Performance tests failed${NC}"
fi

# ============================================
# 4. SECURITY TESTS (OWASP ZAP + Snyk)
# ============================================
echo -e "\n${YELLOW}═══ Stage 4: Security Tests ═══${NC}"

# OWASP ZAP
echo "Running OWASP ZAP security scan..."
cd security-tests/owasp-zap
chmod +x run-security-scan.sh
./run-security-scan.sh || SECURITY_RESULT=1
cp -r security-reports/* "$REPORT_DIR/security-zap" 2>/dev/null || true
cd ../..

# Snyk (if token available)
if [ -n "$SNYK_TOKEN" ]; then
    echo "Running Snyk vulnerability scan..."
    cd security-tests/snyk
    chmod +x snyk-scan.sh
    ./snyk-scan.sh || SECURITY_RESULT=1
    cp -r security-reports/snyk/* "$REPORT_DIR/security-snyk" 2>/dev/null || true
    cd ../..
else
    echo -e "${YELLOW}SNYK_TOKEN not set, skipping Snyk scan${NC}"
fi

if [ $SECURITY_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ Security tests passed${NC}"
else
    echo -e "${RED}❌ Security tests failed${NC}"
fi

# ============================================
# SUMMARY
# ============================================
echo -e "\n${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    TEST SUITE SUMMARY                        ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"

echo -e "\nTest Results:"
[ $UNIT_RESULT -eq 0 ] && echo -e "  Unit & Mutation: ${GREEN}PASSED${NC}" || echo -e "  Unit & Mutation: ${RED}FAILED${NC}"
[ $CONTRACT_RESULT -eq 0 ] && echo -e "  Contract Tests:  ${GREEN}PASSED${NC}" || echo -e "  Contract Tests:  ${RED}FAILED${NC}"
[ $PERFORMANCE_RESULT -eq 0 ] && echo -e "  Performance:     ${GREEN}PASSED${NC}" || echo -e "  Performance:     ${RED}FAILED${NC}"
[ $SECURITY_RESULT -eq 0 ] && echo -e "  Security:        ${GREEN}PASSED${NC}" || echo -e "  Security:        ${RED}FAILED${NC}"

echo -e "\nReports saved to: $REPORT_DIR"

# Exit with error if any test failed
TOTAL_RESULT=$((UNIT_RESULT + CONTRACT_RESULT + PERFORMANCE_RESULT + SECURITY_RESULT))
if [ $TOTAL_RESULT -gt 0 ]; then
    echo -e "\n${RED}⚠️  Some tests failed. Review the reports.${NC}"
    exit 1
fi

echo -e "\n${GREEN}✅ All tests passed successfully!${NC}"








