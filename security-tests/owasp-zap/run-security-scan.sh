#!/bin/bash

# OWASP ZAP Security Scan Runner
# Performs DAST (Dynamic Application Security Testing)

set -e

# Configuration
ZAP_IMAGE="ghcr.io/zaproxy/zaproxy:stable"
TARGET_URL="${TARGET_URL:-http://host.docker.internal:8080}"
REPORT_DIR="$(pwd)/security-reports"
ZAP_CONFIG="$(pwd)/zap-automation.yaml"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=== OWASP ZAP Security Scan ===${NC}"
echo "Target: $TARGET_URL"
echo "Report Directory: $REPORT_DIR"

# Create report directory
mkdir -p "$REPORT_DIR"

# Run ZAP baseline scan (quick)
echo -e "\n${YELLOW}Running Baseline Scan...${NC}"
docker run --rm \
    -v "$REPORT_DIR:/zap/wrk:rw" \
    -t $ZAP_IMAGE zap-baseline.py \
    -t "$TARGET_URL" \
    -g gen.conf \
    -r baseline-report.html \
    -J baseline-report.json \
    -I || true

# Run ZAP API scan
echo -e "\n${YELLOW}Running API Scan...${NC}"
docker run --rm \
    -v "$REPORT_DIR:/zap/wrk:rw" \
    -t $ZAP_IMAGE zap-api-scan.py \
    -t "$TARGET_URL/api-docs" \
    -f openapi \
    -r api-scan-report.html \
    -J api-scan-report.json \
    -I || true

# Run ZAP full scan (comprehensive)
echo -e "\n${YELLOW}Running Full Scan...${NC}"
docker run --rm \
    -v "$REPORT_DIR:/zap/wrk:rw" \
    -v "$ZAP_CONFIG:/zap/wrk/zap-automation.yaml:ro" \
    -t $ZAP_IMAGE zap-full-scan.py \
    -t "$TARGET_URL" \
    -r full-scan-report.html \
    -J full-scan-report.json \
    -I || true

# Run automation framework
echo -e "\n${YELLOW}Running Automation Framework...${NC}"
docker run --rm \
    -v "$REPORT_DIR:/zap/reports:rw" \
    -v "$ZAP_CONFIG:/zap/wrk/zap-automation.yaml:ro" \
    -t $ZAP_IMAGE zap.sh -cmd \
    -autorun /zap/wrk/zap-automation.yaml || true

# Parse results and generate summary
echo -e "\n${GREEN}=== Scan Complete ===${NC}"
echo "Reports generated in: $REPORT_DIR"

# Check for high severity findings
if [ -f "$REPORT_DIR/api-scan-report.json" ]; then
    HIGH_ALERTS=$(cat "$REPORT_DIR/api-scan-report.json" | grep -o '"riskcode": "3"' | wc -l || echo "0")
    MEDIUM_ALERTS=$(cat "$REPORT_DIR/api-scan-report.json" | grep -o '"riskcode": "2"' | wc -l || echo "0")
    LOW_ALERTS=$(cat "$REPORT_DIR/api-scan-report.json" | grep -o '"riskcode": "1"' | wc -l || echo "0")
    
    echo -e "\n${YELLOW}=== Security Summary ===${NC}"
    echo -e "High Risk Alerts: ${RED}$HIGH_ALERTS${NC}"
    echo -e "Medium Risk Alerts: ${YELLOW}$MEDIUM_ALERTS${NC}"
    echo -e "Low Risk Alerts: ${GREEN}$LOW_ALERTS${NC}"
    
    if [ "$HIGH_ALERTS" -gt 0 ]; then
        echo -e "\n${RED}⚠️  HIGH SEVERITY VULNERABILITIES FOUND!${NC}"
        echo "Review the reports immediately."
        exit 1
    fi
fi

echo -e "\n${GREEN}✅ Security scan completed successfully${NC}"

