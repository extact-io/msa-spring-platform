package io.extact.msa.spring.platform.fw.stub.application.server.application;

import lombok.NonNull;

public record RegisterPersonCommand(
        @NonNull String name) {
}
