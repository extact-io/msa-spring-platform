package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.core.generic.Transformable;
import io.extact.msa.spring.platform.fw.stub.server.person.application.PersonAddCommand;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;

record PersonAddRequest(
        @PersonName String name) implements Transformable {

    PersonAddCommand toCommand() {
        return PersonAddCommand.builder()
                .name(name)
                .build();
    }
}
