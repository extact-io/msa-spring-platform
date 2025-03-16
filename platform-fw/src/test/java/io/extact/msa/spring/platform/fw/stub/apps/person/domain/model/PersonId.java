package io.extact.msa.spring.platform.fw.stub.apps.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

public record PersonId(
        @RmsId int id) implements Identity {
}
