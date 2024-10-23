package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPerson;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.constraint.PersonName;

public record TestPersonResponse(
        @RmsId //
        Integer id,
        @PersonName //
        String name) {

    public TestPerson toModel() {
        return TestPerson.reconstruct(id, name);
    }
}
