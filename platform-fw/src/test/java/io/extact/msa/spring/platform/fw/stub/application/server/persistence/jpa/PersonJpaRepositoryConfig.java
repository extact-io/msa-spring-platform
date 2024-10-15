package io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = Person.class)
@EnableJpaRepositories(basePackageClasses = { PersonJpaInnerRepository.class, EntityManagerHolder.class })
@Import(ValidationConfiguration.class)
public class PersonJpaRepositoryConfig {

    @Bean
    PersonJpaRepository personJpaRepository(PersonJpaInnerRepository innerRepository) {
        return new PersonJpaRepository(innerRepository);
    }
}
