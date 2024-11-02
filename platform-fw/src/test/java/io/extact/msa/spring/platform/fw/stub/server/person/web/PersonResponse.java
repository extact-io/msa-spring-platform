package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

record PersonResponse(
        Integer id,
        String name) {

    public static PersonResponse from(Person model) {
        if (model == null) {
            return null;
        }
        return new PersonResponse(model.getId().id(), model.getName());
    }
}
