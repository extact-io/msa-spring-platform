package io.extact.msa.spring.platform.fw.stub.application.server.web;

import io.extact.msa.spring.platform.fw.domain.Transformable;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

record UpdatePersonRequest(
        @RmsId //
        Integer id,
        @PersonName //
        String name) implements Transformable {

    PersonId personId() {
        return new PersonId(this.id);
    }
}
