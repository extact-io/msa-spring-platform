package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.jpa.DefaultJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.DefaultModelEntityMapper;
import io.extact.msa.spring.platform.fw.persistence.jpa.EntityContext;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EmployeeEntity.class)
@EnableJpaRepositories(basePackageClasses = { EmployeeJpaExecutor.class, EntityContext.class })
@Import(ValidationConfiguration.class)
public class EmployeeJpaRepositoryConfig {


    @Bean
    EmployeeJpaRepository employeeJpaRepository(EmployeeJpaExecutor executor) {
        return new EmployeeJpaRepository(
                executor,
                new DefaultModelEntityMapper<>(EmployeeEntity::from));
    }

    @Bean
    DefaultJpaRepository<Employee, EmployeeEntity> defaultEmployeeJpaRepository(
            SpringDataJpaExecutor<EmployeeEntity> executor) {
        return new DefaultJpaRepository<>(
                executor,
                new DefaultModelEntityMapper<>(EmployeeEntity::from));
    }
}
