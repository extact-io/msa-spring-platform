package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import lombok.NonNull;

public record UpdatePersonClientRequest(
        @NonNull Integer id,
        @NonNull String name) {
}
