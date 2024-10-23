package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.persistence.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultJpaRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultModelEntityMapper;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EmployeeEntity.class)
@EnableJpaRepositories(basePackageClasses = EmployeeSpringDataJpa.class)
@Import(ValidationConfiguration.class)
public class EmployeeJpaRepositoryConfig {

//    @Bean
//    EmployeeJpaRepository employeeJpaRepository(EmployeeSpringDataJpa executor) {
//        return new EmployeeJpaRepository(
//                executor,
//                new DefaultModelEntityMapper<>(EmployeeEntity::from));
//    }

    @Bean
    GenericRepository<Employee> defaultJpaRepository(EmployeeSpringDataJpa executor) {
        return new DefaultJpaRepository<Employee, EmployeeEntity>(
                executor,
                new DefaultModelEntityMapper<>(EmployeeEntity::from));
    }
}
