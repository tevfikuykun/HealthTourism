package com.healthtourism.graph.service;

import com.healthtourism.graph.model.PatientNode;
import com.healthtourism.graph.model.ConditionNode;
import com.healthtourism.graph.repository.PatientNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * GraphRAG Service
 * Combines Neo4j (Graph) + Milvus (Vector) for advanced AI context retrieval
 */
@Service
public class GraphRAGService {
    
    @Autowired
    private PatientNodeRepository patientRepository;
    
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Autowired
    private VectorEmbeddingService vectorService;
    
    /**
     * GraphRAG: Find similar patients with conditions and procedures
     * Example: "Diabetic patients who underwent cardiac surgery"
     */
    public List<Map<String, Object>> findSimilarCases(
            String condition, 
            String procedureType,
            int limit) {
        
        String cypherQuery = """
            MATCH (p:Patient)-[:HAS_CONDITION]->(c:Condition {name: $condition})
            MATCH (p)-[:UNDERWENT_PROCEDURE]->(proc:Procedure {type: $procedureType})
            MATCH (p)-[:HAS_CONDITION]->(otherConditions:Condition)
            MATCH (p)-[:TAKES_MEDICATION]->(meds:Medication)
            RETURN p, 
                   collect(DISTINCT otherConditions.name) as conditions,
                   collect(DISTINCT meds.name) as medications,
                   proc.outcome as outcome,
                   proc.riskScore as riskScore
            ORDER BY proc.riskScore ASC
            LIMIT $limit
            """;
        
        return neo4jTemplate.query(cypherQuery, Map.of(
            "condition", condition,
            "procedureType", procedureType,
            "limit", limit
        )).all();
    }
    
    /**
     * Find hidden correlations using graph traversal
     * Example: "What conditions increase risk for X procedure?"
     */
    public List<Map<String, Object>> findRiskCorrelations(String procedureType) {
        String cypherQuery = """
            MATCH (c:Condition)-[:INCREASES_RISK_FOR]->(p:Procedure {type: $procedureType})
            MATCH (patients:Patient)-[:HAS_CONDITION]->(c)
            MATCH (patients)-[:UNDERWENT_PROCEDURE]->(p)
            WITH c, p, count(patients) as patientCount,
                 avg(patients.age) as avgAge,
                 collect(DISTINCT patients.gender) as genders
            RETURN c.name as condition,
                   patientCount,
                   avgAge,
                   genders,
                   p.riskMultiplier as riskMultiplier
            ORDER BY riskMultiplier DESC
            """;
        
        return neo4jTemplate.query(cypherQuery, Map.of(
            "procedureType", procedureType
        )).all();
    }
    
    /**
     * GraphRAG: Combine graph query with vector similarity search
     */
    public String generateAIContext(Long patientId, String query) {
        // 1. Get patient from graph
        PatientNode patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        // 2. Find similar cases using graph
        List<Map<String, Object>> similarCases = findSimilarCases(
            patient.getConditions().get(0).getName(),
            "CARDIAC_SURGERY",
            5
        );
        
        // 3. Get vector embeddings for semantic search
        List<String> vectorContexts = vectorService.searchSimilar(query, 10);
        
        // 4. Combine graph + vector context for AI
        StringBuilder context = new StringBuilder();
        context.append("Patient Graph Context:\n");
        context.append("Conditions: ").append(patient.getConditions()).append("\n");
        context.append("Similar Cases: ").append(similarCases.size()).append("\n");
        context.append("\nVector Search Context:\n");
        context.append(String.join("\n", vectorContexts));
        
        return context.toString();
    }
}

