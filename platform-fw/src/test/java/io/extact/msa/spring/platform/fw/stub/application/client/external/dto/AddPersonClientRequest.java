package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

public record AddPersonClientRequest(
        String name) {

    public Person toEntity() {
        return Person.ofTransient(name);
    }
}
