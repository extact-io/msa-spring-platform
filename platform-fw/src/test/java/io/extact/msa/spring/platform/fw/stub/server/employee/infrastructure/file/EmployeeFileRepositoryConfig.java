package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.file;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.DefaultModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.ModelConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.EmployeeRepository;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;

@TestConfiguration(proxyBeanMethods = false)
@Import(ModelConfig.class)
public class EmployeeFileRepositoryConfig {

    @Bean
    ModelArrayMapper<Employee> employeeArrayMapper(ModelValidator validator) {
        return new EmployeeArrayMapper(modelSupportFactory(validator));
    }

    @Bean
    EmployeeRepository employeeFileRepository(Environment env, ModelArrayMapper<Employee> mapper) {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        FileOperator fileOperator = new FileOperator(pathDeriver.derive(EmployeeFileRepository.FILE_ENTITY));
        return new EmployeeFileRepository(fileOperator, mapper);
    }

    private ModelPropertySupportFactory modelSupportFactory(ModelValidator validator) {
        return new DefaultModelPropertySupportFactory(validator);
    }
}
