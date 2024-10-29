package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultModelEntityMapper;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = PersonEntity.class)
@EnableJpaRepositories(basePackageClasses = PersonSpringDataJpa.class)
@Import(ValidationConfiguration.class)
public class PersonJpaRepositoryConfig {

    @Bean
    PersonJpaRepository personJpaRepository(PersonSpringDataJpa springData) {
        return new PersonJpaRepository(
                springData,
                new DefaultModelEntityMapper<>(PersonEntity::from));
    }
}
