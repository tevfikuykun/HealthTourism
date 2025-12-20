package com.healthtourism.apigateway.controller;

import com.healthtourism.apigateway.config.DynamicRouteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST API for managing routes dynamically
 * Allows adding, updating, and removing routes without service restart
 */
@RestController
@RequestMapping("/api/gateway/routes")
public class DynamicRouteController {

    @Autowired
    private DynamicRouteConfig dynamicRouteConfig;

    @PostMapping
    public Mono<ResponseEntity<String>> addRoute(
            @RequestParam String routeId,
            @RequestParam String path,
            @RequestParam String uri,
            @RequestBody(required = false) Map<String, Object> metadata) {
        return dynamicRouteConfig.addOrUpdateRoute(routeId, path, uri, metadata != null ? metadata : Map.of())
                .then(Mono.just(ResponseEntity.ok("Route added successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage())));
    }

    @PutMapping("/{routeId}")
    public Mono<ResponseEntity<String>> updateRoute(
            @PathVariable String routeId,
            @RequestParam String path,
            @RequestParam String uri,
            @RequestBody(required = false) Map<String, Object> metadata) {
        return dynamicRouteConfig.addOrUpdateRoute(routeId, path, uri, metadata != null ? metadata : Map.of())
                .then(Mono.just(ResponseEntity.ok("Route updated successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage())));
    }

    @DeleteMapping("/{routeId}")
    public Mono<ResponseEntity<String>> removeRoute(@PathVariable String routeId) {
        return dynamicRouteConfig.removeRoute(routeId)
                .then(Mono.just(ResponseEntity.ok("Route removed successfully")))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage())));
    }

    @GetMapping
    public Flux<Map<String, Object>> getAllRoutes() {
        return dynamicRouteConfig.getAllRoutes();
    }
}





