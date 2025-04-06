package io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model;

import java.util.Objects;

import io.extact.msa.spring.platform.fw.domain.model.EntityModelView;

public interface EmployeeModelView extends EntityModelView<EmployeeModelView> {

    EmployeeId getId();

    String getName();

    String getDeptName();

    @Override
    default boolean isEqual(EmployeeModelView other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(getId(), other.getId())
                && Objects.equals(getName(), other.getName())
                && Objects.equals(getDeptName(), other.getDeptName());
    }
}
