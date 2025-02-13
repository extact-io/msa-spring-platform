package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.DefaultModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.ModelConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultJpaRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.DefaultModelEntityMapper;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;

@TestConfiguration(proxyBeanMethods = false)
@EntityScan(basePackageClasses = EmployeeEntity.class)
@EnableJpaRepositories(basePackageClasses = EmployeeJpaRepositoryDelegator.class)
@Import(ModelConfig.class)
public class EmployeeJpaRepositoryConfig {

    @Bean
    GenericRepository<Employee> defaultJpaRepository(EmployeeJpaRepositoryDelegator delegator, ModelValidator validator) {
        return new DefaultJpaRepository<Employee, EmployeeEntity>(
                delegator,
                new DefaultModelEntityMapper<>(
                        EmployeeEntity::from,
                        modelSupportFactory(validator)));
    }

    private ModelPropertySupportFactory modelSupportFactory(ModelValidator validator) {
        return new DefaultModelPropertySupportFactory(validator);
    }
}
