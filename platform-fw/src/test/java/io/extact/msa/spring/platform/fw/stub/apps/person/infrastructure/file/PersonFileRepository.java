package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.file;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import lombok.NonNull;

public class PersonFileRepository extends AbstractFileRepository<Person, PersonId> implements PersonRepository {

    public static final String FILE_ENTITY = "person";

    public PersonFileRepository(FileOperator fileReadWriter, ModelArrayMapper<Person> mapper) {
        super(fileReadWriter, mapper, PersonId::new);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }

    @Override
    public Optional<Person> findDuplicationData(@NonNull Person checkPerson) {
        return this.findAll().stream()
                .filter(person -> person.getName().equals(checkPerson.getName()))
                .findFirst();
    }
}
