package io.extact.msa.spring.platform.fw.stub.application.server.model;

import io.extact.msa.spring.platform.fw.domain.Identity;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;

public record PersonId(
        @RmsId int id) implements Identity {
}
