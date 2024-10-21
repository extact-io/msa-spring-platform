package io.extact.msa.spring.platform.fw.stub.application.server.application;

import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonId;
import lombok.NonNull;

public record EditPersonCommand(
        @NonNull PersonId id,
        @NonNull String name) {
}
