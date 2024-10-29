package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.file;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

public class PersonalArrayMapper implements ModelArrayMapper<Person> {

    public static final PersonalArrayMapper INSTANCE = new PersonalArrayMapper();

    @Override
    public Person toModel(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        return Person.reconstruct(id, name);
    }

    @Override
    public String[] toArray(Person person) {
        String[] attributes = new String[2];
        attributes[0] = String.valueOf(person.getId().id());
        attributes[1] = person.getName();
        return attributes;
    }
}
