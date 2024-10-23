package io.extact.msa.spring.platform.fw.stub.server.person.infrastrucure.file;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.NonNull;

public class PersonFileRepository extends AbstractFileRepository<Person> implements PersonRepository {

    static final String FILE_ENTITY = "person";

    public PersonFileRepository(FileOperator fileReadWriter, ModelArrayMapper<Person> converter) {
        super(fileReadWriter, converter);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }

    @Override
    public int nextIdentity() {
        return getNextSequence();
    }

    @Override
    public Optional<Person> findName(@NonNull String name) {
        return this.findAll().stream()
                .filter(person -> person.getName().equals(name))
                .findFirst();
    }
}
