package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.spring.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.persistence.file.EntityArrayMapper;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.PersonRepository;

public class PersonFileRepository extends AbstractFileRepository<Person> implements PersonRepository {

    static final String FILE_ENTITY = "person";

    public PersonFileRepository(FileOperator fileReadWriter, EntityArrayMapper<Person> converter) {
        super(fileReadWriter, converter);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }
}
