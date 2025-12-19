#!/bin/bash

# Snyk Security Scan
# Scans for vulnerabilities in dependencies and code

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=== Snyk Security Scan ===${NC}"

# Check if Snyk is installed
if ! command -v snyk &> /dev/null; then
    echo "Installing Snyk CLI..."
    npm install -g snyk
fi

# Authenticate (requires SNYK_TOKEN environment variable)
if [ -z "$SNYK_TOKEN" ]; then
    echo -e "${YELLOW}Warning: SNYK_TOKEN not set. Using interactive auth...${NC}"
    snyk auth
fi

REPORT_DIR="$(pwd)/security-reports/snyk"
mkdir -p "$REPORT_DIR"

# Scan Maven dependencies
echo -e "\n${YELLOW}Scanning Maven dependencies...${NC}"
for pom in $(find ../microservices -name "pom.xml" -type f); do
    SERVICE_DIR=$(dirname "$pom")
    SERVICE_NAME=$(basename "$SERVICE_DIR")
    
    echo "Scanning $SERVICE_NAME..."
    cd "$SERVICE_DIR"
    
    snyk test --json > "$REPORT_DIR/${SERVICE_NAME}-deps.json" 2>/dev/null || true
    snyk test --sarif > "$REPORT_DIR/${SERVICE_NAME}-deps.sarif" 2>/dev/null || true
    
    cd - > /dev/null
done

# Scan Docker images
echo -e "\n${YELLOW}Scanning Docker images...${NC}"
IMAGES=(
    "healthtourism/api-gateway:latest"
    "healthtourism/auth-service:latest"
    "healthtourism/user-service:latest"
)

for image in "${IMAGES[@]}"; do
    IMAGE_NAME=$(echo "$image" | cut -d'/' -f2 | cut -d':' -f1)
    echo "Scanning $IMAGE_NAME..."
    
    snyk container test "$image" --json > "$REPORT_DIR/${IMAGE_NAME}-container.json" 2>/dev/null || true
done

# Scan IaC (Infrastructure as Code)
echo -e "\n${YELLOW}Scanning Infrastructure as Code...${NC}"
snyk iac test ../kubernetes/ --json > "$REPORT_DIR/iac-kubernetes.json" 2>/dev/null || true
snyk iac test ../terraform/ --json > "$REPORT_DIR/iac-terraform.json" 2>/dev/null || true

# Code scan (SAST)
echo -e "\n${YELLOW}Scanning source code (SAST)...${NC}"
snyk code test ../microservices --json > "$REPORT_DIR/code-scan.json" 2>/dev/null || true

# Generate summary
echo -e "\n${GREEN}=== Snyk Scan Summary ===${NC}"

TOTAL_VULNS=0
HIGH_VULNS=0
MEDIUM_VULNS=0
LOW_VULNS=0

for report in "$REPORT_DIR"/*.json; do
    if [ -f "$report" ]; then
        VULNS=$(cat "$report" | grep -o '"severity": "high"' | wc -l || echo "0")
        HIGH_VULNS=$((HIGH_VULNS + VULNS))
        
        VULNS=$(cat "$report" | grep -o '"severity": "medium"' | wc -l || echo "0")
        MEDIUM_VULNS=$((MEDIUM_VULNS + VULNS))
        
        VULNS=$(cat "$report" | grep -o '"severity": "low"' | wc -l || echo "0")
        LOW_VULNS=$((LOW_VULNS + VULNS))
    fi
done

TOTAL_VULNS=$((HIGH_VULNS + MEDIUM_VULNS + LOW_VULNS))

echo -e "Total Vulnerabilities: $TOTAL_VULNS"
echo -e "High: ${RED}$HIGH_VULNS${NC}"
echo -e "Medium: ${YELLOW}$MEDIUM_VULNS${NC}"
echo -e "Low: ${GREEN}$LOW_VULNS${NC}"

echo -e "\nReports saved to: $REPORT_DIR"

if [ "$HIGH_VULNS" -gt 0 ]; then
    echo -e "\n${RED}⚠️  HIGH SEVERITY VULNERABILITIES FOUND!${NC}"
    exit 1
fi

echo -e "\n${GREEN}✅ Snyk scan completed${NC}"



