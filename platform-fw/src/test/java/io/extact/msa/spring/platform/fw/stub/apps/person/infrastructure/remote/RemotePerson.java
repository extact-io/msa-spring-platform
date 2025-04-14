package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.PhysicalEntity;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person.PersonCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;

@JsonIgnoreProperties("pk")
public record RemotePerson(
        Integer id,
        String name
        ) implements PhysicalEntity<Person>, PersonCreatable {

    public static RemotePerson from(Person model) {
        return new RemotePerson(
                model.getId().id(),
                model.getName());
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Person toModel(ModelValidator validator) {
        Person person = newInstance(new PersonId(id), name);
        person.configure(validator);
        return person;
    }
}
