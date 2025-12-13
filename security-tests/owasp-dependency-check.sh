#!/bin/bash

echo "=========================================="
echo "Running OWASP Dependency Check"
echo "=========================================="

# Check if OWASP Dependency Check is installed
if ! command -v dependency-check.sh &> /dev/null; then
    echo "OWASP Dependency Check is not installed."
    echo "Installing..."
    
    # Download OWASP Dependency Check
    VERSION="9.0.9"
    wget https://github.com/jeremylong/DependencyCheck/releases/download/v${VERSION}/dependency-check-${VERSION}-release.zip
    unzip dependency-check-${VERSION}-release.zip
    export PATH=$PATH:$(pwd)/dependency-check/bin
    
    echo "OWASP Dependency Check installed!"
fi

cd "$(dirname "$0")/.."

# Run dependency check on all services
echo "Scanning microservices for vulnerabilities..."

for service_dir in microservices/*/; do
    if [ -f "${service_dir}pom.xml" ]; then
        service_name=$(basename "$service_dir")
        echo "Scanning $service_name..."
        dependency-check.sh --project "$service_name" --scan "${service_dir}" --format HTML --out "security-tests/owasp-reports/${service_name}-report.html"
    fi
done

echo "Dependency check completed! Reports saved in security-tests/owasp-reports/"

