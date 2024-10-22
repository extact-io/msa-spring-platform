package io.extact.msa.spring.platform.fw.stub.application.server.application;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.service.ApplicationServiceSupport;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonDuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonFactory;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class PersonApplicationService implements ApplicationServiceSupport<Person> {

    private final PersonFactory factory;
    private final PersonDuplicateChecker duplicateChecker;
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
        person.changeName(command.name());
        duplicateChecker.check(person);
        repository.update(person);
        return person;
    }

    public GenericRepository<Person> getRepository() {
        return this.repository;
    }
}
