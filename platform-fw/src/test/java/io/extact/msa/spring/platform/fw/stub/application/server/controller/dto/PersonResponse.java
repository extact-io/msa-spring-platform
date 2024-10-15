package io.extact.msa.spring.platform.fw.stub.application.server.controller.dto;

import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

public record PersonResponse(
        Integer id,
        String name) {

    public static PersonResponse from(Person entity) {
        if (entity == null) {
            return null;
        }
        return new PersonResponse(entity.getId(), entity.getName());
    }

    public Person toEntity() {
        return Person.valueOf(id, name);
    }
}
