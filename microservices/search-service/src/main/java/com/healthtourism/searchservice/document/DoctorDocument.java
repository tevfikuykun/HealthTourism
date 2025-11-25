package com.healthtourism.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDocument {
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String firstName;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String lastName;
    
    @Field(type = FieldType.Text)
    private String specialty;
    
    @Field(type = FieldType.Double)
    private Double rating;
    
    @Field(type = FieldType.Text)
    private String languages;
    
    @Field(type = FieldType.Integer)
    private Integer experienceYears;
}


