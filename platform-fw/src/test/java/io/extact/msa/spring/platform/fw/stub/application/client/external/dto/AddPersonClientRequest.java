package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import lombok.NonNull;

public record AddPersonClientRequest(
        @NonNull String name) {
}
