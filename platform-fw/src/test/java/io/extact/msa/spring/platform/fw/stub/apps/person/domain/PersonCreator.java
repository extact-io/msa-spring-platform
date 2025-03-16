package io.extact.msa.spring.platform.fw.stub.apps.person.domain;

import io.extact.msa.spring.platform.fw.domain.model.ModelCreator;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonCreator implements ModelCreator<Person, String> {

    private final IdentityGenerator idGenerator;
    private final ModelValidator validator;
    private final PersonCreatable constructorProxy = new PersonCreatable() {};

    public Person create(String name) {

        PersonId id = new PersonId(idGenerator.nextIdentity());
        Person person = constructorProxy.newInstance(id, name);

        person.configure(validator);
        validator.validateModel(person);

        return person;
    }
}
