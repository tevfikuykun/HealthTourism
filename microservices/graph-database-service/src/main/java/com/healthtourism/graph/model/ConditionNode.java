package com.healthtourism.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * Medical Condition Node
 */
@Node("Condition")
@Data
public class ConditionNode {
    @Id
    private String conditionId;
    private String name;
    private String category;
    private String severity;
    
    @Relationship(type = "INCREASES_RISK_FOR", direction = Relationship.Direction.OUTGOING)
    private List<ProcedureNode> riskyProcedures;
    
    @Relationship(type = "REQUIRES_MEDICATION", direction = Relationship.Direction.OUTGOING)
    private List<MedicationNode> requiredMedications;
}

