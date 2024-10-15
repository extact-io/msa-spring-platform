package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.persistence.file.EntityArrayMapper;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

public class PersonalArrayMapper implements EntityArrayMapper<Person> {

    public static final PersonalArrayMapper INSTANCE = new PersonalArrayMapper();

    @Override
    public Person toEntity(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        return Person.valueOf(id, name);
    }

    @Override
    public String[] toArray(Person person) {
        String[] attributes = new String[2];
        attributes[0] = String.valueOf(person.getId());
        attributes[1] = person.getName();
        return attributes;
    }
}
