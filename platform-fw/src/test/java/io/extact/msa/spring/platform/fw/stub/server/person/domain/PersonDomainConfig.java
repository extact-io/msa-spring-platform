package io.extact.msa.spring.platform.fw.stub.server.person.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.domain.service.SimpleDuplicateChecker;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class PersonDomainConfig {

    @Bean
    PersonCreator personnCreator(PersonRepository idGenerator, ModelValidator validator) {
        return new PersonCreator(idGenerator, validator);
    }

    @Bean
    DuplicateChecker<Person> personDuplicateChecker(PersonRepository repository) {
        return new SimpleDuplicateChecker<Person>(repository);
    }
}
