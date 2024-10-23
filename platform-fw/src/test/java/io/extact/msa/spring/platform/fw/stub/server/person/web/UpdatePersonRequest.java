package io.extact.msa.spring.platform.fw.stub.server.person.web;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Transformable;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;

record UpdatePersonRequest(
        @RmsId //
        Integer id,
        @PersonName //
        String name) implements Transformable {

    PersonId personId() {
        return new PersonId(this.id);
    }
}
