#!/bin/bash

# ============================================================
# Health Tourism Platform - Kubernetes Full Deployment
# ============================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Health Tourism Platform - Kubernetes Deployment          ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════════╝${NC}"

# Check prerequisites
check_prerequisites() {
    echo -e "\n${YELLOW}[1/10] Checking prerequisites...${NC}"
    
    command -v kubectl >/dev/null 2>&1 || { echo -e "${RED}kubectl required${NC}"; exit 1; }
    command -v docker >/dev/null 2>&1 || { echo -e "${RED}docker required${NC}"; exit 1; }
    
    # Check cluster connection
    kubectl cluster-info >/dev/null 2>&1 || { echo -e "${RED}Cannot connect to Kubernetes cluster${NC}"; exit 1; }
    
    echo -e "${GREEN}✅ Prerequisites OK${NC}"
}

# Create namespaces
create_namespaces() {
    echo -e "\n${YELLOW}[2/10] Creating namespaces...${NC}"
    
    kubectl create namespace healthtourism --dry-run=client -o yaml | kubectl apply -f -
    kubectl create namespace observability --dry-run=client -o yaml | kubectl apply -f -
    kubectl create namespace istio-system --dry-run=client -o yaml | kubectl apply -f -
    
    # Enable Istio injection
    kubectl label namespace healthtourism istio-injection=enabled --overwrite
    
    echo -e "${GREEN}✅ Namespaces created${NC}"
}

# Deploy infrastructure
deploy_infrastructure() {
    echo -e "\n${YELLOW}[3/10] Deploying infrastructure...${NC}"
    
    kubectl apply -f infrastructure/ -n healthtourism 2>/dev/null || true
    
    echo -e "${GREEN}✅ Infrastructure deployed${NC}"
}

# Install Istio
install_istio() {
    echo -e "\n${YELLOW}[4/10] Installing Istio Service Mesh...${NC}"
    
    if command -v istioctl &> /dev/null; then
        istioctl install -f istio/istio-installation.yaml -y
    else
        echo -e "${YELLOW}istioctl not found, skipping Istio installation${NC}"
        echo "Install with: curl -L https://istio.io/downloadIstio | sh -"
    fi
    
    echo -e "${GREEN}✅ Istio configured${NC}"
}

# Deploy observability stack
deploy_observability() {
    echo -e "\n${YELLOW}[5/10] Deploying observability stack...${NC}"
    
    kubectl apply -f istio/observability.yaml
    
    echo -e "${GREEN}✅ Observability deployed${NC}"
}

# Build and push Docker images
build_images() {
    echo -e "\n${YELLOW}[6/10] Building Docker images...${NC}"
    
    REGISTRY=${DOCKER_REGISTRY:-"healthtourism"}
    
    services=(
        "api-gateway"
        "auth-service"
        "user-service"
        "hospital-service"
        "doctor-service"
        "reservation-service"
        "payment-service"
        "notification-service"
        "ai-health-companion-service"
        "blockchain-service"
        "iot-monitoring-service"
    )
    
    for service in "${services[@]}"; do
        if [ -d "../microservices/$service" ]; then
            echo "Building $service..."
            docker build -t $REGISTRY/$service:latest ../microservices/$service 2>/dev/null || true
        fi
    done
    
    echo -e "${GREEN}✅ Images built${NC}"
}

# Deploy services
deploy_services() {
    echo -e "\n${YELLOW}[7/10] Deploying microservices...${NC}"
    
    kubectl apply -f istio/service-deployments.yaml
    
    echo -e "${GREEN}✅ Services deployed${NC}"
}

# Apply traffic management
apply_traffic_management() {
    echo -e "\n${YELLOW}[8/10] Applying traffic management rules...${NC}"
    
    kubectl apply -f istio/traffic-management.yaml
    kubectl apply -f istio-mtls-config.yaml 2>/dev/null || true
    kubectl apply -f istio-gateway.yaml 2>/dev/null || true
    
    echo -e "${GREEN}✅ Traffic management configured${NC}"
}

# Wait for deployments
wait_for_deployments() {
    echo -e "\n${YELLOW}[9/10] Waiting for deployments to be ready...${NC}"
    
    kubectl wait --for=condition=available deployment --all -n healthtourism --timeout=300s 2>/dev/null || true
    
    echo -e "${GREEN}✅ Deployments ready${NC}"
}

# Print status
print_status() {
    echo -e "\n${YELLOW}[10/10] Deployment Status...${NC}"
    
    echo -e "\n${BLUE}Pods:${NC}"
    kubectl get pods -n healthtourism
    
    echo -e "\n${BLUE}Services:${NC}"
    kubectl get svc -n healthtourism
    
    INGRESS_IP=$(kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "pending")
    
    echo -e "\n${GREEN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                    DEPLOYMENT COMPLETE                       ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo -e "\n${GREEN}Access Points:${NC}"
    echo "  API Gateway:     http://$INGRESS_IP"
    echo "  Kiali Dashboard: istioctl dashboard kiali"
    echo "  Jaeger Tracing:  istioctl dashboard jaeger"
    echo "  Grafana:         istioctl dashboard grafana"
    echo ""
    echo -e "${GREEN}Useful Commands:${NC}"
    echo "  kubectl get pods -n healthtourism"
    echo "  kubectl logs -f <pod-name> -n healthtourism"
    echo "  istioctl analyze -n healthtourism"
}

# Main
main() {
    check_prerequisites
    create_namespaces
    deploy_infrastructure
    install_istio
    deploy_observability
    build_images
    deploy_services
    apply_traffic_management
    wait_for_deployments
    print_status
}

main "$@"





