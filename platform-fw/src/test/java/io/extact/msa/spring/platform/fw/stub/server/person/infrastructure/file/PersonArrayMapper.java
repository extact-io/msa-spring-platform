package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.file;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonArrayMapper implements ModelArrayMapper<Person>, PersonCreatable {

    private final ModelPropertySupportFactory modelSupportFactory;

    @Override
    public Person toModel(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        Person person = newInstance(new PersonId(id), name);
        person.configureSupport(modelSupportFactory);
        return person;
    }

    @Override
    public String[] toArray(Person person) {
        String[] attributes = new String[2];
        attributes[0] = String.valueOf(person.getId().id());
        attributes[1] = person.getName();
        return attributes;
    }
}
