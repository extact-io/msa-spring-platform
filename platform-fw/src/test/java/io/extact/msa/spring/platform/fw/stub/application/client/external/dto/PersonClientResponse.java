package io.extact.msa.spring.platform.fw.stub.application.client.external.dto;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonName;

public record PersonClientResponse(
        @RmsId //
        Integer id,
        @PersonName //
        String name) {
}
