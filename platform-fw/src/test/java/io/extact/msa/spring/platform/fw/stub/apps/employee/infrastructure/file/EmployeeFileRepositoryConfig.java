package io.extact.msa.spring.platform.fw.stub.apps.employee.infrastructure.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.EmployeeRepository;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class EmployeeFileRepositoryConfig {

    @Bean
    ModelArrayMapper<Employee> employeeArrayMapper(ModelValidator validator) {
        return new EmployeeArrayMapper(validator);
    }

    @Bean
    EmployeeRepository employeeFileRepository(Environment env, ModelArrayMapper<Employee> mapper) {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        FileOperator fileOperator = new FileOperator(pathDeriver.derive(EmployeeFileRepository.FILE_ENTITY));
        return new EmployeeFileRepository(fileOperator, mapper);
    }
}
