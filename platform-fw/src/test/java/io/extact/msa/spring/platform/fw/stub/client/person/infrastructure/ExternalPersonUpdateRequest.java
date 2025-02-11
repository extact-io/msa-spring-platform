package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import lombok.NonNull;

record ExternalPersonUpdateRequest(
        @NonNull Integer id,
        @NonNull String name) {

    static ExternalPersonUpdateRequest from(ExternalPerson model) {
        return new ExternalPersonUpdateRequest(
                model.getId().id(),
                model.getName());
    }
}
