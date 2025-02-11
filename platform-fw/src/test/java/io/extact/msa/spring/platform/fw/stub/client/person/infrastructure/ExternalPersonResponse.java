package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson.ExternalPersonCreatable;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;

public record ExternalPersonResponse(
        @RmsId //
        Integer id,
        @PersonName //
        String name) implements ExternalPersonCreatable {

    public ExternalPerson toModel() {
        return newInstance(new ExternalPersonId(id), name);
    }
}
