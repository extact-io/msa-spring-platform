package io.extact.msa.spring.platform.fw.stub.apps.person.application;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record PersonAddCommand(
        @NonNull String name) {
}
