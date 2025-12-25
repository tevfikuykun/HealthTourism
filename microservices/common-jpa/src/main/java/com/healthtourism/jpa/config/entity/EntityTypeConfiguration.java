package com.healthtourism.jpa.config.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;

/**
 * Entity Type Configuration Interface
 * 
 * Similar to Entity Framework's IEntityTypeConfiguration<T>
 * Allows defining entity configurations (indexes, constraints, etc.) 
 * in separate classes instead of cluttering the entity class itself.
 * 
 * @param <T> Entity type
 */
public interface EntityTypeConfiguration<T> {
    
    /**
     * Configure the entity
     * 
     * @param metadata Hibernate metadata
     * @param table Table mapping for the entity
     */
    void configure(Metadata metadata, Table table);
    
    /**
     * Get the entity class this configuration applies to
     * 
     * @return Entity class
     */
    Class<T> getEntityType();
    
    /**
     * Helper method to create index on a column
     * 
     * @param table Table to add index to
     * @param indexName Name of the index
     * @param columnName Name of the column to index
     * @param unique Whether the index should be unique
     */
    default void createIndex(Table table, String indexName, String columnName, boolean unique) {
        Index index = new Index();
        index.setName(indexName);
        index.setTable(table);
        index.addColumn(table.getColumn(new org.hibernate.mapping.Column(columnName)));
        if (unique) {
            index.setUnique(true);
        }
        table.addIndex(index);
    }
    
    /**
     * Helper method to create composite index
     * 
     * @param table Table to add index to
     * @param indexName Name of the index
     * @param columnNames Names of the columns to index
     * @param unique Whether the index should be unique
     */
    default void createCompositeIndex(Table table, String indexName, String[] columnNames, boolean unique) {
        Index index = new Index();
        index.setName(indexName);
        index.setTable(table);
        for (String columnName : columnNames) {
            index.addColumn(table.getColumn(new org.hibernate.mapping.Column(columnName)));
        }
        if (unique) {
            index.setUnique(true);
        }
        table.addIndex(index);
    }
}

