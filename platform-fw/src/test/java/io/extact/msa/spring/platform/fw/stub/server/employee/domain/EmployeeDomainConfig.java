package io.extact.msa.spring.platform.fw.stub.server.employee.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.domain.service.SimpleDuplicateChecker;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class EmployeeDomainConfig {

    @Bean
    EmployeeCreator personnCreator(
            EmployeeRepository idGenerator,
            ModelValidator validator) {
        return new EmployeeCreator(idGenerator, validator);
    }

    @Bean
    DuplicateChecker<Person> employeeDuplicateChecker(PersonRepository repository) {
        return new SimpleDuplicateChecker<Person>(repository);
    }
}
