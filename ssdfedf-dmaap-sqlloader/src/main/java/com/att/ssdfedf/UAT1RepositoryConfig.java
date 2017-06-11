package com.lokesh.lokeshedf;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableScheduling
@EnableJpaRepositories(
        entityManagerFactoryRef = "uat1EntityManagerFactory", 
        transactionManagerRef = "uat1TransactionManager",
        basePackages = { "com.lokesh.lokeshedf.repository.uat" })
@EnableTransactionManagement
public class UAT1RepositoryConfig {
	
	
	@Bean(name = "uat1DataSource")
    @ConfigurationProperties(prefix="spring.uat1.datasource")
    public DataSource uat1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "uat1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean uat1EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("uat1DataSource") DataSource uat1DataSource) {
        return builder
                .dataSource(uat1DataSource)
                .packages("com.lokesh.lokeshedf.domain")
                .persistenceUnit("uat1")
                .build();
    }

    @Bean(name = "uat1TransactionManager")
    public PlatformTransactionManager uat1TransactionManager(
            @Qualifier("uat1EntityManagerFactory") EntityManagerFactory uat1EntityManagerFactory) {
        return new JpaTransactionManager(uat1EntityManagerFactory);
    }

}
