package io.extact.msa.spring.platform.fw.stub.server.person.application;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonCreator;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

@TestConfiguration(proxyBeanMethods = false)
public class PersonServiceConfig {

    @Bean
    PersonService personService(
            PersonCreator modelCreator,
            DuplicateChecker<Person> duplicateChecker,
            PersonRepository repository) {
        return new PersonService(modelCreator, duplicateChecker, repository);
    }
}
