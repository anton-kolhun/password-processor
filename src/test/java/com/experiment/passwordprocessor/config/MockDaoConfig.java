package com.experiment.passwordprocessor.config;

import com.experiment.passwordprocessor.dal.repository.TokenInfoRepository;
import liquibase.integration.spring.SpringLiquibase;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public class MockDaoConfig {

    @Bean
    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean
    public SpringLiquibase liquibase() {
        return Mockito.mock(SpringLiquibase.class);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Mockito.mock(EntityManagerFactory.class);
    }

    @Bean
    public TokenInfoRepository tokenInfoRepository() {
        return Mockito.mock(TokenInfoRepository.class);
    }


}
