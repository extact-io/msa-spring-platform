package io.extact.msa.spring.platform.fw.stub.apps.person.web;

import io.extact.msa.spring.platform.core.generic.Transformable;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.apps.person.application.PersonUpdateCommand;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.constraint.PersonName;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
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
