package com.lokesh.lokeshedf;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableScheduling
@EnableJpaRepositories(
        entityManagerFactoryRef = "st1EntityManagerFactory", 
        transactionManagerRef = "st1TransactionManager",
        basePackages = { "com.lokesh.lokeshedf.repository.st" })
@EnableTransactionManagement
public class ST1RepositoryConfig {
	
	@Bean(name = "dataSource")
	@Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "st1EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean st1EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.lokesh.lokeshedf.domain")
                .persistenceUnit("st1")
                .build();
    }

    @Bean(name = "st1TransactionManager")
    public PlatformTransactionManager st1TransactionManager(
            @Qualifier("st1EntityManagerFactory") EntityManagerFactory st1EntityManagerFactory) {
        return new JpaTransactionManager(st1EntityManagerFactory);
    }

}
