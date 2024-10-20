package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.file;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.persistence.file.io.LoadPathDeriver;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeRepository;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidationConfiguration.class)
public class EmployeeFileRepositoryConfig {

    @Bean
    FileOperator fileOperator(Environment env) throws IOException {
        LoadPathDeriver pathDeriver = new LoadPathDeriver(env);
        return new FileOperator(pathDeriver.derive(EmployeeFileRepository.FILE_ENTITY));
    }

    @Bean
    ModelArrayMapper<Employee> employeeArrayMapper() {
        return EmployeeArrayMapper.INSTANCE;
    }

    @Bean
    @Primary
    EmployeeRepository employeeFileRepository(FileOperator fileOperator, ModelArrayMapper<Employee> mapper) {
        return new EmployeeFileRepository(fileOperator, mapper);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("prototype") // for unit test
    EmployeeRepository prototypePersonFileRepository(Environment env) throws IOException {
        FileOperator fileOperator = fileOperator(env); // Bean生成の都度ファイルを再配置する
        return new EmployeeFileRepository(fileOperator, employeeArrayMapper());
    }
}
