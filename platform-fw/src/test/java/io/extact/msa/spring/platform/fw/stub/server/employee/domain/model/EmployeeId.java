package io.extact.msa.spring.platform.fw.stub.server.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.Identity;

public record EmployeeId(
        @RmsId int id) implements Identity {
}
