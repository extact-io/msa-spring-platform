package io.extact.msa.spring.platform.fw.stub.application.server.web;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

public record UpdatePersonRequest(
        @RmsId //
        Integer id,
        @PersonName //
        String name) {

    public static UpdatePersonRequest from(Person entity) {
        if (entity == null) {
            return null;
        }
        return new UpdatePersonRequest(entity.getId(), entity.getName());
    }

    public Person toEntity() {
        return Person.valueOf(id, name);
    }
}
