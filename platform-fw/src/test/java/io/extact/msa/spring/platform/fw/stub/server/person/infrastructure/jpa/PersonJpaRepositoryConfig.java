package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.DefaultModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.ModelConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultModelEntityMapper;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = PersonEntity.class)
@EnableJpaRepositories(basePackageClasses = PersonJpaRepositoryDelegator.class)
@Import(ModelConfig.class)
public class PersonJpaRepositoryConfig {

    @Bean
    PersonJpaRepository personJpaRepository(PersonJpaRepositoryDelegator delegator, ModelValidator validator) {
        return new PersonJpaRepository(
                delegator,
                new DefaultModelEntityMapper<>(
                        PersonEntity::from,
                        modelSupportFactory(validator)));
    }

    private ModelPropertySupportFactory modelSupportFactory(ModelValidator validator) {
        return new DefaultModelPropertySupportFactory(validator);
    }
}
