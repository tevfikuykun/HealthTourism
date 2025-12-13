#!/bin/bash

echo "=========================================="
echo "Checking Security Headers"
echo "=========================================="

BASE_URL=${BASE_URL:-"http://localhost:8080"}

check_header() {
    local header_name=$1
    local url=$2
    local response=$(curl -s -I "$url" | grep -i "$header_name")
    
    if [ -z "$response" ]; then
        echo "❌ Missing: $header_name"
        return 1
    else
        echo "✅ Found: $response"
        return 0
    fi
}

echo "Checking security headers for $BASE_URL..."

# Check security headers
check_header "X-Content-Type-Options" "$BASE_URL/api/hospitals"
check_header "X-Frame-Options" "$BASE_URL/api/hospitals"
check_header "X-XSS-Protection" "$BASE_URL/api/hospitals"
check_header "Strict-Transport-Security" "$BASE_URL/api/hospitals"
check_header "Content-Security-Policy" "$BASE_URL/api/hospitals"
check_header "Referrer-Policy" "$BASE_URL/api/hospitals"

echo "Security headers check completed!"

