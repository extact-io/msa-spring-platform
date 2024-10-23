package io.extact.msa.spring.platform.fw.stub.server.person.application;

import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.PersonId;
import lombok.NonNull;

public record EditPersonCommand(
        @NonNull PersonId id,
        @NonNull String name) {
}
