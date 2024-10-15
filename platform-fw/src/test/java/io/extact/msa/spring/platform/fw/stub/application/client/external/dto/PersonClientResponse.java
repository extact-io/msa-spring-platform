package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import jakarta.validation.constraints.Min;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;

public record PersonClientResponse(
        @RmsId //
        Integer id,
        //@PersonName //
        @Min(100)
        String name) {

    public static PersonClientResponse from(Person entity) {
        if (entity == null) {
            return null;
        }
        return new PersonClientResponse(entity.getId(), entity.getName());
    }

    public Person toEntity() {
        return Person.valueOf(id, name);
    }
}
