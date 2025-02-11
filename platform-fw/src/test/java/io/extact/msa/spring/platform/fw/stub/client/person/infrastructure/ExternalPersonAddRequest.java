package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import io.extact.msa.spring.platform.fw.stub.client.person.domain.model.ExternalPerson;
import lombok.NonNull;

record ExternalPersonAddRequest(
        @NonNull String name) {

    static ExternalPersonAddRequest from(ExternalPerson model) {
        return new ExternalPersonAddRequest(model.getName());
    }
}
