package io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.AbstractEntityModel;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Employee extends AbstractEntityModel implements EmployeeModelView {

    private @NonNull EmployeeId id;
    private @NonNull String name;
    private @NonNull String deptName;

    Employee(EmployeeId id, String name, String deptName) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
    }

    public void editEmployee(String newName, String newDeptName) {
        applyName(newName);
        applyDeptName(newDeptName);
    }

    private void applyName(String newName) {
        Employee test = new Employee();
        test.name = newName;
        validator().validateField(test, this::getName);
        this.name = newName;
    }

    private void applyDeptName(String newDeptName) {
        Employee test = new Employee();
        test.deptName = newDeptName;
        validator().validateField(test, test::getDeptName);
        this.deptName = newDeptName;
    }

    public interface EmployeeCreatable {
        default Employee newInstance(
                EmployeeId id,
                String name,
                String deptName) {
            return new Employee(id, name, deptName);
        }
    }
}
