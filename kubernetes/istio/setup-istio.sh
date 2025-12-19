#!/bin/bash

# Istio Service Mesh Setup Script
# For Health Tourism Platform (25+ microservices)

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Istio Service Mesh Setup - Health Tourism Platform       ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"

# Check prerequisites
check_prerequisites() {
    echo -e "\n${YELLOW}Checking prerequisites...${NC}"
    
    if ! command -v kubectl &> /dev/null; then
        echo -e "${RED}kubectl not found. Please install kubectl first.${NC}"
        exit 1
    fi
    
    if ! command -v istioctl &> /dev/null; then
        echo -e "${YELLOW}istioctl not found. Installing...${NC}"
        curl -L https://istio.io/downloadIstio | sh -
        export PATH=$PWD/istio-*/bin:$PATH
    fi
    
    echo -e "${GREEN}✅ Prerequisites met${NC}"
}

# Install Istio
install_istio() {
    echo -e "\n${YELLOW}Installing Istio...${NC}"
    
    # Install with custom configuration
    istioctl install -f istio-installation.yaml -y
    
    # Verify installation
    kubectl get pods -n istio-system
    
    echo -e "${GREEN}✅ Istio installed${NC}"
}

# Create namespace
create_namespace() {
    echo -e "\n${YELLOW}Creating namespace with Istio injection...${NC}"
    kubectl apply -f namespace.yaml
    echo -e "${GREEN}✅ Namespace created${NC}"
}

# Deploy observability stack
deploy_observability() {
    echo -e "\n${YELLOW}Deploying observability stack...${NC}"
    kubectl apply -f observability.yaml
    
    # Wait for pods
    kubectl wait --for=condition=ready pod -l app=jaeger -n observability --timeout=120s
    
    echo -e "${GREEN}✅ Observability stack deployed${NC}"
}

# Deploy traffic management
deploy_traffic_management() {
    echo -e "\n${YELLOW}Configuring traffic management...${NC}"
    kubectl apply -f traffic-management.yaml
    echo -e "${GREEN}✅ Traffic management configured${NC}"
}

# Deploy services
deploy_services() {
    echo -e "\n${YELLOW}Deploying services...${NC}"
    kubectl apply -f service-deployments.yaml
    
    # Wait for deployments
    kubectl wait --for=condition=available deployment --all -n healthtourism --timeout=300s
    
    echo -e "${GREEN}✅ Services deployed${NC}"
}

# Print access information
print_access_info() {
    echo -e "\n${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║                    ACCESS INFORMATION                        ║${NC}"
    echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"
    
    echo -e "\n${GREEN}Dashboards:${NC}"
    echo "  Kiali (3D Service Map):  istioctl dashboard kiali"
    echo "  Jaeger (Tracing):        istioctl dashboard jaeger"
    echo "  Grafana (Metrics):       istioctl dashboard grafana"
    echo "  Prometheus:              istioctl dashboard prometheus"
    
    echo -e "\n${GREEN}Chaos Engineering:${NC}"
    echo "  ./chaos-engineering-commands.sh help"
    echo "  ./chaos-engineering-commands.sh experiment auth-slowdown"
    
    echo -e "\n${GREEN}Service Mesh Status:${NC}"
    echo "  istioctl analyze -n healthtourism"
    echo "  istioctl proxy-status"
    
    INGRESS_IP=$(kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
    echo -e "\n${GREEN}Ingress Gateway:${NC} http://${INGRESS_IP:-localhost}"
}

# Main installation
main() {
    check_prerequisites
    install_istio
    create_namespace
    deploy_observability
    deploy_traffic_management
    deploy_services
    print_access_info
    
    echo -e "\n${GREEN}✅ Istio Service Mesh setup complete!${NC}"
}

# Run
main "$@"




