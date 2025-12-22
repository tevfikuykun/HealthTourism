package com.healthtourism.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * Medical Procedure Node
 */
@Node("Procedure")
@Data
public class ProcedureNode {
    @Id
    private String procedureId;
    private String type;
    private String name;
    private Double riskScore;
    private Double riskMultiplier;
    private String outcome;
    
    @Relationship(type = "INCREASES_RISK_FOR", direction = Relationship.Direction.INCOMING)
    private List<ConditionNode> riskConditions;
}






