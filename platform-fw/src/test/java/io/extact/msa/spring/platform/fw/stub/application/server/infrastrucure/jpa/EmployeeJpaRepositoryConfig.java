package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = Employee.class)
@EnableJpaRepositories(basePackageClasses = { EmployeeJpaInnerRepository.class, EntityManagerHolder.class })
@Import(ValidationConfiguration.class)
public class EmployeeJpaRepositoryConfig {

    @Bean
    EmployeeJpaRepository employeeJpaRepository(EmployeeJpaInnerRepository innerRepository) {
        return new EmployeeJpaRepository(innerRepository);
    }
}
