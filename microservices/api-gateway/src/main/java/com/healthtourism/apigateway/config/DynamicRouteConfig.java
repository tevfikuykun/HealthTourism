package com.healthtourism.apigateway.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis-based Dynamic Route Management
 * Routes can be updated at runtime without service restart
 */
@Configuration
public class DynamicRouteConfig implements RouteDefinitionRepository {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;
    
    @Value("${gateway.dynamic-routes.redis-key-prefix:gateway:routes:}")
    private String routePrefix;

    public DynamicRouteConfig(
            ReactiveRedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            ApplicationEventPublisher publisher) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return redisTemplate.keys(routePrefix + "*")
                .flatMap(key -> redisTemplate.opsForValue().get(key))
                .map(json -> {
                    try {
                        Map<String, Object> routeData = objectMapper.readValue(json, 
                            new TypeReference<Map<String, Object>>() {});
                        RouteDefinition definition = new RouteDefinition();
                        definition.setId((String) routeData.get("id"));
                        definition.setUri(java.net.URI.create((String) routeData.get("uri")));
                        // Add predicates and filters from metadata
                        return definition;
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing route definition", e);
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Error loading routes from Redis: " + e.getMessage());
                    return Flux.empty();
                });
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(rd -> {
            Map<String, Object> routeData = new HashMap<>();
            routeData.put("id", rd.getId());
            routeData.put("uri", rd.getUri().toString());
            routeData.put("predicates", rd.getPredicates());
            routeData.put("filters", rd.getFilters());
            
            try {
                String json = objectMapper.writeValueAsString(routeData);
                return redisTemplate.opsForValue()
                        .set(routePrefix + rd.getId(), json)
                        .then();
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Error saving route", e));
            }
        }).then();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> redisTemplate.delete(routePrefix + id).then());
    }

    /**
     * Add or update a route dynamically
     */
    public Mono<Void> addOrUpdateRoute(String routeId, String path, String uri, Map<String, Object> metadata) {
        Map<String, Object> routeData = new HashMap<>();
        routeData.put("id", routeId);
        routeData.put("path", path);
        routeData.put("uri", uri);
        routeData.put("metadata", metadata);
        
        try {
            String json = objectMapper.writeValueAsString(routeData);
            return redisTemplate.opsForValue()
                    .set(routePrefix + routeId, json)
                    .then(Mono.fromRunnable(() -> publisher.publishEvent(new RefreshRoutesEvent(this))))
                    .then();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error saving route", e));
        }
    }

    /**
     * Remove a route dynamically
     */
    public Mono<Void> removeRoute(String routeId) {
        return redisTemplate.delete(routePrefix + routeId)
                .then(Mono.fromRunnable(() -> publisher.publishEvent(new RefreshRoutesEvent(this))))
                .then();
    }

    /**
     * Get all active routes
     */
    public Flux<Map<String, Object>> getAllRoutes() {
        return redisTemplate.keys(routePrefix + "*")
                .flatMap(key -> redisTemplate.opsForValue().get(key)
                        .map(json -> {
                            try {
                                return objectMapper.readValue(json, 
                                    new TypeReference<Map<String, Object>>() {});
                            } catch (Exception e) {
                                return new HashMap<String, Object>();
                            }
                        }));
    }
}

