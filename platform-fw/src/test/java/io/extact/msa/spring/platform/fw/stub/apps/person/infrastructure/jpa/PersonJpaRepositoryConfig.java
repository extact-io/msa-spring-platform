package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.DefaultModelEntityMapper;

@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = PersonEntity.class)
@EnableJpaRepositories(basePackageClasses = PersonJpaRepositoryDelegator.class)
@Import(ValidatorConfig.class)
public class PersonJpaRepositoryConfig {

    @Bean
    PersonJpaRepository personJpaRepository(PersonJpaRepositoryDelegator delegator, ModelValidator validator) {
        return new PersonJpaRepository(
                delegator,
                new DefaultModelEntityMapper<>(PersonEntity::from, validator));
    }
}
