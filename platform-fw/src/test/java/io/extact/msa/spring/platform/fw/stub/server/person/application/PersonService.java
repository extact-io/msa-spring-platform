package io.extact.msa.spring.platform.fw.stub.server.person.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import io.extact.msa.spring.platform.fw.application.ApplicationCrudSupport;
import io.extact.msa.spring.platform.fw.domain.service.DuplicateChecker;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonCreator;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonReference;

@Transactional
public class PersonService {

    private final PersonCreator modelCreator;
    private final ApplicationCrudSupport<Person> support;

    public PersonService(
            PersonCreator modelCreator,
            DuplicateChecker<Person> duplicateChecker,
            PersonRepository repository) {

        this.modelCreator = modelCreator;
        this.support = new ApplicationCrudSupport<>(duplicateChecker, repository);
    }

    public List<PersonReference> getAll() {
        return new ArrayList<>(support.getAll()); // 型をReferenceに制限するため変換
    }

    public Optional<Person> getById(PersonId id) {
        return support.getById(id);
    }

    public PersonReference add(PersonAddCommand command) {
        return support.add(() -> this.createModel(command));
    }

    public PersonReference update(PersonUpdateCommand command) {
        return support.update(command.id(), person -> this.editModel(person, command));
    }

    public void delete(PersonId id) {
        support.delete(id);
    }

    private Person createModel(PersonAddCommand command) {
        return modelCreator.create(command.name());
    }

    private void editModel(Person person, PersonUpdateCommand command) {
        person.editName(command.name());
    }
}
