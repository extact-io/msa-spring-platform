package io.extact.msa.spring.platform.fw.stub.application.server.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonFactory {

    private final PersonRepository repository;

    public Person create(String name) {
        return new Person(new PersonId(repository.nextIdentity()), name);
    }
}
