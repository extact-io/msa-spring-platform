package io.extact.msa.spring.platform.fw.stub.server.person.domain;

import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person.PersonCreatable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonFactory implements PersonCreatable {

    private final PersonRepository repository;

    public Person create(String name) {
        return newInstance(new PersonId(repository.nextIdentity()), name);
    }
}
