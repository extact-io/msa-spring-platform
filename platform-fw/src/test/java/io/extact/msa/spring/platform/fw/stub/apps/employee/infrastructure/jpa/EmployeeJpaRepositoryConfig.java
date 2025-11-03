package io.extact.msa.spring.platform.fw.stub.apps.employee.infrastructure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.DefaultModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultJpaRepository;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.EmployeeId;

@Configuration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EmployeeEntity.class)
@EnableJpaRepositories(basePackageClasses = EmployeeJpaRepositoryDelegator.class)
@Import(ValidatorConfig.class)
public class EmployeeJpaRepositoryConfig {

    @Bean
    GenericRepository<Employee> defaultJpaRepository(EmployeeJpaRepositoryDelegator delegator,
            ModelValidator validator) {

        return new DefaultJpaRepository<Employee, EmployeeId, EmployeeEntity>(
                delegator,
                new DefaultModelEntityMapper<>(EmployeeEntity::from, validator),
                EmployeeId::new);
    }
}
