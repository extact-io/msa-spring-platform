package io.extact.msa.spring.platform.fw.stub.server.person.application;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.application.ApplicationServiceSupport;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonFactory;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class PersonApplicationService implements ApplicationServiceSupport<Person> {

    private final PersonFactory factory;
    private final DuplicateChecker<Person> duplicateChecker;
    private final PersonRepository repository;

    public Person register(RegisterPersonCommand command) {
        Person person = factory.create(command.name());
        duplicateChecker.check(person);
        repository.add(person);
        return person;
    }

    public Person edit(EditPersonCommand command) {
        Person person = repository.find(command.id())
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        person.editName(command.name());
        duplicateChecker.check(person);
        repository.update(person);
        return person;
    }

    public GenericRepository<Person> getRepository() {
        return this.repository;
    }
}
