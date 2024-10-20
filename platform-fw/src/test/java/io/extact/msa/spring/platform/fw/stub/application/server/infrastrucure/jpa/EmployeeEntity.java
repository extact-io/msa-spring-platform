package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import static jakarta.persistence.AccessType.*;

import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Delete;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.spring.platform.fw.persistence.jpa.TableEntity;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeId;
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
public class EmployeeEntity implements TableEntity<Employee> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = { Update.class, Delete.class })
    private Integer id;
    private String name;
    private String deptName;

    public static EmployeeEntity from(Employee model) {
        return new EmployeeEntity(model.getId().id(), model.getName(), model.getDeptName());
    }

    @Override
    public Employee toModel() {
        return Employee.reconstruct(new EmployeeId(id), name, deptName);
    }
}
