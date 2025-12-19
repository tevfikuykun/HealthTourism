package com.healthtourism.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * Patient Node in Knowledge Graph
 */
@Node("Patient")
@Data
public class PatientNode {
    @Id
    private Long patientId;
    private String name;
    private Integer age;
    private String gender;
    
    @Relationship(type = "HAS_CONDITION", direction = Relationship.Direction.OUTGOING)
    private List<ConditionNode> conditions;
    
    @Relationship(type = "UNDERWENT_PROCEDURE", direction = Relationship.Direction.OUTGOING)
    private List<ProcedureNode> procedures;
    
    @Relationship(type = "TAKES_MEDICATION", direction = Relationship.Direction.OUTGOING)
    private List<MedicationNode> medications;
}


