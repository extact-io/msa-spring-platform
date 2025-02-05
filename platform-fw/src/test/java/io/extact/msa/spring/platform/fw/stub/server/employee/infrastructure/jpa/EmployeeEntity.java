package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.jpa;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.TableEntity;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee.EmployeeCreatable;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.EmployeeId;
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
public class EmployeeEntity implements TableEntity<Employee>, EmployeeCreatable {

    @Id
    private Integer id;
    private String name;
    private String deptName;

    public static EmployeeEntity from(Employee model) {
        return new EmployeeEntity(model.getId().id(), model.getName(), model.getDeptName());
    }

    @Override
    public Employee toModel(ModelPropertySupportFactory factory) {
        Employee employee = newInstance(new EmployeeId(this.id), this.name, this.deptName);
        employee.configureSupport(factory);
        return employee;
    }
}
