package com.healthtourism.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Medication Node
 */
@Node("Medication")
@Data
public class MedicationNode {
    @Id
    private String medicationId;
    private String name;
    private String dosage;
    private String frequency;
}



