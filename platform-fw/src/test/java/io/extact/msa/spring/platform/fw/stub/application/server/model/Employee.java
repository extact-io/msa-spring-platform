package io.extact.msa.spring.platform.fw.stub.application.server.model;

import io.extact.msa.spring.platform.fw.domain.DomainModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
public class Employee implements DomainModel {

    private @NonNull EmployeeId id;
    private @NonNull @NonFinal String name;
    private @NonNull @NonFinal String deptName;

    public static Employee reconstruct(EmployeeId id, String name, String deptName) {
        return new Employee(id, name, deptName);
    }

    public void changeEditableFields(String name, String deptName) {
        this.name = name;
        this.deptName = deptName;
    }
}
