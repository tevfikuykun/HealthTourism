package com.healthtourism.jpa.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate Configuration
 * L1/L2 Caching, Dirty Checking, Envers Audit
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.healthtourism.jpa.repository")
@EnableTransactionManagement
public class HibernateConfig {
    
    @Value("${spring.jpa.properties.hibernate.cache.use_second_level_cache:true}")
    private boolean useSecondLevelCache;
    
    @Value("${spring.jpa.properties.hibernate.cache.use_query_cache:true}")
    private boolean useQueryCache;
    
    @Value("${spring.jpa.properties.hibernate.cache.region.factory_class:org.hibernate.cache.redis.hibernate52.SingletonRedisRegionFactory}")
    private String cacheRegionFactory;
    
    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.redis.port:6379}")
    private int redisPort;
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        Map<String, Object> properties = new HashMap<>();
        
        // Hibernate Settings
        properties.put(AvailableSettings.HBM2DDL_AUTO, "validate");
        properties.put(AvailableSettings.SHOW_SQL, false);
        properties.put(AvailableSettings.FORMAT_SQL, true);
        
        // L2 Cache Configuration (Redis via Spring Cache)
        properties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, useSecondLevelCache);
        properties.put(AvailableSettings.USE_QUERY_CACHE, useQueryCache);
        // Using Spring Cache abstraction with Redis
        // Cache region factory will be configured via Spring Cache
        
        // Dirty Checking (enabled by default)
        properties.put(AvailableSettings.AUTO_FLUSH, true);
        properties.put(AvailableSettings.FLUSH_MODE, "AUTO");
        
        // Hibernate Envers (Audit)
        properties.put("org.hibernate.envers.audit_table_suffix", "_audit");
        properties.put("org.hibernate.envers.revision_field_name", "revision");
        properties.put("org.hibernate.envers.revision_type_field_name", "revision_type");
        properties.put("org.hibernate.envers.store_data_at_delete", true);
        properties.put("org.hibernate.envers.default_schema", "audit");
        
        // Performance
        properties.put(AvailableSettings.JDBC_BATCH_SIZE, 50);
        properties.put(AvailableSettings.ORDER_INSERTS, true);
        properties.put(AvailableSettings.ORDER_UPDATES, true);
        
        em.setJpaPropertyMap(properties);
        
        return em;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}

