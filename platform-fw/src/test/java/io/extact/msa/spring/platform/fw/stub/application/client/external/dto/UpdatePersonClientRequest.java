package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

public record UpdatePersonClientRequest(
        Integer id,
        String name) {

    public static UpdatePersonClientRequest from(Person entity) {
        if (entity == null) {
            return null;
        }
        return new UpdatePersonClientRequest(entity.getId(), entity.getName());
    }

    public Person toEntity() {
        return Person.valueOf(id, name);
    }
}
