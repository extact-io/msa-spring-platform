package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.PersonName;

public record PersonClientResponse(
        @RmsId //
        Integer id,
        @PersonName //
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
