package com.product.reward.config.database;

import com.product.reward.artist.service.DatabaseDataInitiator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    // todo: querydsl-sql
    @Bean
    public com.querydsl.sql.Configuration configuration() {
        return new com.querydsl.sql.Configuration(new MySQLTemplates());
    }

//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }

    @Bean
    @ConditionalOnProperty(name = "app.init-db-datas", havingValue = "true")
    public ApplicationRunner databaseInitiator(ApplicationContext context) {
        return args -> {
            DatabaseDataInitiator initiator = context.getBean(DatabaseDataInitiator.class);
            initiator.init();
        };
    }
}