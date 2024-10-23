package io.extact.msa.spring.platform.fw.stub.client.person.domain.model;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

public record TestPersonId(
        @RmsId int id) implements Identity {
}
