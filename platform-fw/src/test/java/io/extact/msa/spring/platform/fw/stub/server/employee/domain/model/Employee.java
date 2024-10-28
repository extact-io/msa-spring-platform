package io.extact.msa.spring.platform.fw.stub.server.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
@Getter
public class Employee implements DomainModel {

    private final @NonNull EmployeeId id;
    private @NonNull String name;
    private @NonNull String deptName;

    public static Employee reconstruct(int id, String name, String deptName) {
        return new Employee(new EmployeeId(id), name, deptName);
    }

    public void editNameAndDept(String name, String deptName) {
        this.name = name;
        this.deptName = deptName;
    }
}
