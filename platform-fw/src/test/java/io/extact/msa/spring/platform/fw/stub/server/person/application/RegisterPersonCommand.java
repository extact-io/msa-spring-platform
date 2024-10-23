package io.extact.msa.spring.platform.fw.stub.server.person.application;

import lombok.NonNull;

public record RegisterPersonCommand(
        @NonNull String name) {
}
