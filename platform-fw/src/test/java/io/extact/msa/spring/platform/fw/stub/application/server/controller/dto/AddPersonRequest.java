package io.extact.msa.spring.platform.fw.stub.application.server.controller.dto;

import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.PersonName;

public record AddPersonRequest(
        @PersonName //
        String name) {

    public Person toEntity() {
        return Person.ofTransient(name);
    }
}
