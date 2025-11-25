package com.healthtourism.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDocument {
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text)
    private String address;
    
    @Field(type = FieldType.Text)
    private String city;
    
    @Field(type = FieldType.Double)
    private Double rating;
    
    @Field(type = FieldType.Text)
    private String specialties;
    
    @Field(type = FieldType.Double)
    private Double airportDistance;
}


