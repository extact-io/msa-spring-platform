package io.extact.msa.spring.platform.fw.stub.server.employee.domain.model;

import io.extact.msa.spring.platform.fw.domain.model.EntityModel;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupport;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
public class Employee implements EntityModel, EmployeeReference {

    private @NonNull EmployeeId id;
    private @NonNull String name;
    private @NonNull String deptName;

    @ToString.Exclude
    private ModelPropertySupport modelSupport;

    Employee(EmployeeId id, String name, String deptName) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
    }

    public void editEmployee(String newName, String newDeptName) {
        modelSupport.setPropertyWithValidation("name", newName);
        modelSupport.setPropertyWithValidation("deptName", newDeptName);
    }

    @Override
    public void configureSupport(ModelPropertySupportFactory factory) {
        this.modelSupport = factory.create(Employee::new, this);
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
