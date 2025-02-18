package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.file;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee.EmployeeCreatable;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.EmployeeId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeArrayMapper implements ModelArrayMapper<Employee>, EmployeeCreatable {

    private final ModelValidator validator;

    @Override
    public Employee toModel(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        String deptName = attributes[2];
        Employee employee = newInstance(new EmployeeId(id), name, deptName);
        employee.configure(validator);
        return employee;
    }

    @Override
    public String[] toArray(Employee model) {
        String[] attributes = new String[3];
        attributes[0] = String.valueOf(model.getId().id());
        attributes[1] = model.getName();
        attributes[1] = model.getDeptName();
        return attributes;
    }
}
