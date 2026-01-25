package io.extact.msa.spring.platform.fw.stub.apps.person.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.service.DomainEventPublisher;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.feature.event.EventPublisherConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonCreator;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

@Configuration(proxyBeanMethods = false)
@Import(EventPublisherConfig.class)
public class PersonServiceConfig {

    @Bean
    PersonService personService(
            PersonCreator modelCreator,
            DuplicateChecker<Person> duplicateChecker,
            PersonRepository repository,
            DomainEventPublisher eventPublisher) {
        return new PersonService(modelCreator, duplicateChecker, repository, eventPublisher);
    }
}
