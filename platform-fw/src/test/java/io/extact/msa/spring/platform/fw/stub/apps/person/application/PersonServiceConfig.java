package io.extact.msa.spring.platform.fw.stub.apps.person.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonCreator;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

@Configuration(proxyBeanMethods = false)
public class PersonServiceConfig {

    @Bean
    PersonService personService(
            PersonCreator modelCreator,
            DuplicateChecker<Person> duplicateChecker,
            PersonRepository repository) {
        return new PersonService(modelCreator, duplicateChecker, repository);
    }
}
