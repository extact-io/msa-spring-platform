package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import lombok.NonNull;

public record AddTestPersonRequest(
        @NonNull String name) {
}
