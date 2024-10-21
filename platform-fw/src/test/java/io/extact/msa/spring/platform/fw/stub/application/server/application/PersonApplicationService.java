package io.extact.msa.spring.platform.fw.stub.application.server.application;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonDuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonFactory;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class PersonApplicationService {

    private final PersonFactory factory;
    private final PersonDuplicateChecker duplicateChecker;
    private final PersonRepository repository;

    public Optional<Person> getById(PersonId personId) {
        return repository.find(personId);
    }

    public List<Person> getAll() {
        return repository.findAll();
    }

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

    public void delete(PersonId id) {
        Person person = repository.find(id)
                .orElseThrow(() -> new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND));
        repository.delete(person);
    }

    public GenericRepository<Person> getRepository() {
        return this.repository;
    }
}
