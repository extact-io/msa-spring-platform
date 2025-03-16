package io.extact.msa.spring.platform.fw.stub.apps.person.application;

import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record PersonUpdateCommand(
        @NonNull PersonId id,
        @NonNull String name) {
}
