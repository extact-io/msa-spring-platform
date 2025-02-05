package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonReference;

record PersonResponse(
        Integer id,
        String name) {

    public static PersonResponse from(PersonReference model) {
        if (model == null) {
            return null;
        }
        return new PersonResponse(
                model.getId().id(),
                model.getName());
    }
}
