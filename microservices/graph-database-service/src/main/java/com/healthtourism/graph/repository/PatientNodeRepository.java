package com.healthtourism.graph.repository;

import com.healthtourism.graph.model.PatientNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientNodeRepository extends Neo4jRepository<PatientNode, Long> {
    Optional<PatientNode> findByPatientId(Long patientId);
    
    @Query("MATCH (p:Patient)-[:HAS_CONDITION]->(c:Condition {name: $conditionName}) RETURN p")
    List<PatientNode> findPatientsWithCondition(String conditionName);
    
    @Query("MATCH (p:Patient)-[:UNDERWENT_PROCEDURE]->(proc:Procedure {type: $procedureType}) RETURN p")
    List<PatientNode> findPatientsWithProcedure(String procedureType);
}



