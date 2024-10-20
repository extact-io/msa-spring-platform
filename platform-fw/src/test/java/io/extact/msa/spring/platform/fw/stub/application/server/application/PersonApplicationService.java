package io.extact.msa.spring.platform.fw.stub.application.server.application;

import java.util.List;
import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonDuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonFactory;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonApplicationService {

    private final PersonFactory factory;
    private final PersonDuplicateChecker duplicateChecker;
    private final PersonRepository repository;

    public Optional<Person> find(PersonId personId) {
        return repository.find(personId);
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Person add(AddPersonCommand command) {
        Person person = factory.create(command.name());
        duplicateChecker.check(person);
        repository.add(person);
        return find(person.getId()).get();
    }

    public GenericRepository<Person> getRepository() {
        return this.repository;
    }
}
