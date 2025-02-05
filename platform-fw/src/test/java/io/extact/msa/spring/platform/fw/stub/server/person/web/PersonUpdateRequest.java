package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Transformable;
import io.extact.msa.spring.platform.fw.stub.server.person.application.PersonUpdateCommand;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import lombok.Builder;

@Builder
record PersonUpdateRequest(
        @RmsId Integer id,
        @PersonName String name) implements Transformable {

    PersonUpdateCommand toCommand() {
        return PersonUpdateCommand.builder()
                .id(new PersonId(id))
                .name(name)
                .build();
    }
}
