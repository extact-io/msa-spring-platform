package io.extact.msa.spring.platform.fw.stub.server.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelReference;

public interface EmployeeReference extends EntityModelReference {

    EmployeeId getId();
    String getName();
    String getDeptName();
}
