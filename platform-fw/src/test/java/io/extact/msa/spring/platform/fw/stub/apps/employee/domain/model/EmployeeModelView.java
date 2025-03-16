package io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelView;

public interface EmployeeModelView extends EntityModelView {

    EmployeeId getId();
    String getName();
    String getDeptName();
}
