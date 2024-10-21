package io.extact.msa.spring.platform.fw.stub.application.server.web;

import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;

public record PersonResponse(
        Integer id,
        String name) {

    public static PersonResponse from(Person model) {
        if (model == null) {
            return null;
        }
        return new PersonResponse(model.getId().id(), model.getName());
    }
}
