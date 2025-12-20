package com.healthtourism.dashboard.service;

import com.healthtourism.dashboard.model.IntegrationStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IntegrationHealthService {
    
    private final WebClient webClient;
    private final Map<String, IntegrationStatus> statusCache = new ConcurrentHashMap<>();
    
    public IntegrationHealthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("")
            .build();
        
        // Initial check on startup
        checkAllIntegrations();
    }
    
    // All integrations configuration
    private static final List<IntegrationStatus> INTEGRATIONS = Arrays.asList(
        // Security & Cryptography
        createIntegration("HashiCorp Vault", "Security", "http://localhost:8200/v1/sys/health", 8200),
        createIntegration("Quantum-Safe Cryptography", "Security", null, null),
        createIntegration("AES-256-GCM Encryption", "Security", null, null),
        
        // Integration & Messaging
        createIntegration("Apache Camel", "Integration", "http://localhost:8091/actuator/health", 8091),
        createIntegration("Apache Kafka", "Integration", "http://localhost:9092", 9092),
        createIntegration("RabbitMQ", "Integration", "http://localhost:15672/api/overview", 5672),
        createIntegration("MQTT", "Integration", null, 1883),
        createIntegration("WebSocket", "Integration", "http://localhost:8031/actuator/health", 8031),
        
        // Database & Storage
        createIntegration("PostgreSQL", "Database", null, 5432),
        createIntegration("MySQL", "Database", null, 3306),
        createIntegration("MongoDB", "Database", null, 27017),
        createIntegration("Neo4j", "Database", "http://localhost:7474", 7474),
        createIntegration("Redis", "Database", null, 6379),
        createIntegration("Elasticsearch", "Database", "http://localhost:9200", 9200),
        createIntegration("Milvus", "Database", null, 19530),
        
        // Observability & Monitoring
        createIntegration("Zipkin", "Observability", "http://localhost:9411", 9411),
        createIntegration("OpenTelemetry", "Observability", null, null),
        createIntegration("Prometheus", "Observability", "http://localhost:9090", 9090),
        createIntegration("Grafana", "Observability", "http://localhost:3000", 3000),
        
        // Architecture & Patterns
        createIntegration("Event Sourcing (Axon)", "Architecture", "http://localhost:8124/actuator/health", 8124),
        createIntegration("CQRS", "Architecture", null, null),
        createIntegration("GraphQL Gateway", "Architecture", "http://localhost:8090/actuator/health", 8090),
        createIntegration("Hibernate/JPA", "Architecture", null, null),
        createIntegration("Semantic Versioning", "Architecture", "http://localhost:8092/actuator/health", 8092),
        createIntegration("Micro-Frontends", "Architecture", null, null),
        
        // AI & Machine Learning
        createIntegration("Apache Flink", "AI/ML", "http://localhost:8081", 8081),
        createIntegration("Confidential Computing", "AI/ML", null, null),
        createIntegration("AIOps (Keptn)", "AI/ML", null, null),
        
        // Infrastructure & Orchestration
        createIntegration("Kubernetes", "Infrastructure", null, null),
        createIntegration("Istio Service Mesh", "Infrastructure", null, null)
    );
    
    private static IntegrationStatus createIntegration(String name, String category, String url, Integer port) {
        IntegrationStatus status = new IntegrationStatus();
        status.setName(name);
        status.setCategory(category);
        status.setUrl(url);
        status.setPort(port);
        status.setStatus(IntegrationStatus.Status.UNKNOWN);
        return status;
    }
    
    @Scheduled(fixedRate = 10000) // Check every 10 seconds
    public void checkAllIntegrations() {
        INTEGRATIONS.parallelStream().forEach(this::checkIntegration);
    }
    
    private void checkIntegration(IntegrationStatus integration) {
        if (integration.getUrl() == null) {
            // For integrations without HTTP endpoint, check port
            integration.setStatus(checkPort(integration.getPort()) ? 
                IntegrationStatus.Status.UP : IntegrationStatus.Status.DOWN);
            integration.setMessage("Port check");
        } else {
            long startTime = System.currentTimeMillis();
            try {
                String response = webClient.get()
                    .uri(integration.getUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
                
                long responseTime = System.currentTimeMillis() - startTime;
                integration.setStatus(IntegrationStatus.Status.UP);
                integration.setResponseTime(responseTime);
                integration.setMessage("OK");
            } catch (Exception e) {
                integration.setStatus(IntegrationStatus.Status.DOWN);
                integration.setMessage(e.getMessage());
            }
        }
        
        integration.setLastChecked(LocalDateTime.now());
        statusCache.put(integration.getName(), integration);
    }
    
    private boolean checkPort(Integer port) {
        if (port == null) return false;
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("localhost", port), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<IntegrationStatus> getAllStatuses() {
        // Check all if cache is empty
        if (statusCache.isEmpty()) {
            checkAllIntegrations();
        }
        return new ArrayList<>(statusCache.values());
    }
    
    public Map<String, Long> getStatistics() {
        List<IntegrationStatus> statuses = getAllStatuses();
        long up = statuses.stream().filter(s -> s.getStatus() == IntegrationStatus.Status.UP).count();
        long down = statuses.stream().filter(s -> s.getStatus() == IntegrationStatus.Status.DOWN).count();
        long unknown = statuses.stream().filter(s -> s.getStatus() == IntegrationStatus.Status.UNKNOWN).count();
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) statuses.size());
        stats.put("up", up);
        stats.put("down", down);
        stats.put("unknown", unknown);
        return stats;
    }
}

