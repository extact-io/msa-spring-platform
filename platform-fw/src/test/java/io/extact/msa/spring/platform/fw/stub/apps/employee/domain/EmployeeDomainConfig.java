package io.extact.msa.spring.platform.fw.stub.apps.employee.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.domain.service.SimpleDuplicateChecker;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class EmployeeDomainConfig {

    @Bean
    EmployeeCreator personnCreator(
            EmployeeRepository idProvider,
            ModelValidator validator) {
        return new EmployeeCreator(idProvider, validator);
    }

    @Bean
    DuplicateChecker<Person> employeeDuplicateChecker(PersonRepository repository) {
        return new SimpleDuplicateChecker<Person>(repository);
    }
}
