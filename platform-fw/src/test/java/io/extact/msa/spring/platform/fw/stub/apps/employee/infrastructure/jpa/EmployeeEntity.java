package io.extact.msa.spring.platform.fw.stub.apps.employee.infrastructure.jpa;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.PhysicalEntity;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee.EmployeeCreatable;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.EmployeeId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Access(FIELD)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class EmployeeEntity implements PhysicalEntity<Employee>, EmployeeCreatable {

    @Id
    private Integer id;
    private String name;
    private String deptName;

    public static EmployeeEntity from(Employee model) {
        return new EmployeeEntity(model.getId().id(), model.getName(), model.getDeptName());
    }

    @Override
    public Employee toModel(ModelValidator validator) {
        Employee employee = newInstance(new EmployeeId(this.id), this.name, this.deptName);
        employee.configure(validator);
        return employee;
    }
}
