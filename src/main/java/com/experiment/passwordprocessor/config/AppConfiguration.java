package com.experiment.passwordprocessor.config;

import com.experiment.passwordprocessor.dal.repository.TokenInfoHistoryRepository;
import com.experiment.passwordprocessor.dal.repository.TokenInfoRepository;
import com.experiment.passwordprocessor.service.TokenProcessorService;
import com.experiment.passwordprocessor.service.helper.DataEncryptionHelper;
import com.experiment.passwordprocessor.service.helper.TokenGenerationHelper;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

@Configuration
@EnableSwagger2
public class AppConfiguration {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setChangeLog("classpath:/liquibase/master-changelog.xml");
        return springLiquibase;
    }

    @Bean
    public DataEncryptionHelper dataEncryptionService(@Value("${encryption.key}") String secretKey) {
        return new DataEncryptionHelper(secretKey);
    }

    @Bean
    public TokenProcessorService tokenProcessorService(@Value("${token.ttl}") Long duration,
                                                       TokenInfoRepository tokenInfoRepository,
                                                       TokenInfoHistoryRepository historyRepository,
                                                       TokenGenerationHelper tokenGenerationHelper) {
        return new TokenProcessorService(tokenInfoRepository, historyRepository,
                tokenGenerationHelper, duration);
    }

    @Bean
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.experiment.passwordprocessor"))
                .paths(PathSelectors.any())
                .build();
    }


}
