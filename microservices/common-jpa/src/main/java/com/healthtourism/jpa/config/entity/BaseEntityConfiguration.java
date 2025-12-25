package com.healthtourism.jpa.config.entity;

import com.healthtourism.jpa.entity.BaseEntity;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Component;

/**
 * Base Entity Configuration
 * 
 * Common configuration for all entities extending BaseEntity.
 * Configures indexes on audit fields and soft delete flag.
 */
@Component
public class BaseEntityConfiguration implements EntityTypeConfiguration<BaseEntity> {
    
    @Override
    public void configure(Metadata metadata, Table table) {
        // Index on isDeleted for efficient soft delete queries
        createIndex(table, "idx_" + table.getName() + "_is_deleted", "is_deleted", false);
        
        // Index on created_at for time-based queries
        createIndex(table, "idx_" + table.getName() + "_created_at", "created_at", false);
        
        // Index on updated_at for time-based queries
        createIndex(table, "idx_" + table.getName() + "_updated_at", "updated_at", false);
        
        // Composite index on created_by and is_deleted for user-specific queries
        createCompositeIndex(table, "idx_" + table.getName() + "_created_by_deleted", 
            new String[]{"created_by", "is_deleted"}, false);
    }
    
    @Override
    public Class<BaseEntity> getEntityType() {
        return BaseEntity.class;
    }
}

