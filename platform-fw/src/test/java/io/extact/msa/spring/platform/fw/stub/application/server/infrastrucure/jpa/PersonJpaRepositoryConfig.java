package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.jpa.DefaultModelEntityMapper;
import io.extact.msa.spring.platform.fw.persistence.jpa.EntityContext;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = Person.class)
@EnableJpaRepositories(basePackageClasses = { PersonJpaExecutor.class, EntityContext.class })
@Import(ValidationConfiguration.class)
public class PersonJpaRepositoryConfig {

    @Bean
    PersonJpaRepository personJpaRepository(PersonJpaExecutor executor) {
        return new PersonJpaRepository(
                executor,
                new DefaultModelEntityMapper<>(PersonEntity::from));
    }
}
