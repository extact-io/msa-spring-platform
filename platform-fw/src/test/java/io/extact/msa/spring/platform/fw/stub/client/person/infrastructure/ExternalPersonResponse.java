package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.infrastructure.external.HttpEntity;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.constraint.PersonName;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson.ExternalPersonCreatable;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPersonId;

public record ExternalPersonResponse(
        @RmsId //
        Integer id,
        @PersonName //
        String name) implements ExternalPersonCreatable, HttpEntity<ExternalPerson> {

    @Override
    public ExternalPerson toModel() {
        return newInstance(new ExternalPersonId(id), name);
    }
}
