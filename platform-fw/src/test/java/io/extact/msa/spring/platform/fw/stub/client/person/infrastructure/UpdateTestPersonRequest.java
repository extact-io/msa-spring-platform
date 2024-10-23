package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.TestPerson;
import lombok.NonNull;

public record UpdateTestPersonRequest(
        @NonNull Integer id,
        @NonNull String name) {

    public static UpdateTestPersonRequest from(TestPerson model) {
        return new UpdateTestPersonRequest(model.getId().id(), model.getName());
    }
}
