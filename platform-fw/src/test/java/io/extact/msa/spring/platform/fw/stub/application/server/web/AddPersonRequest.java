package io.extact.msa.spring.platform.fw.stub.application.server.web;

import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

public record AddPersonRequest(
        @PersonName //
        String name) {

    public Person toEntity() {
        return Person.ofTransient(name);
    }
}
