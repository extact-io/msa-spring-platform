package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.persistence.file;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;

public class EmployeeArrayMapper implements ModelArrayMapper<Employee> {

    public static final EmployeeArrayMapper INSTANCE = new EmployeeArrayMapper();

    @Override
    public Employee toModel(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        String deptName = attributes[2];
        return Employee.reconstruct(id, name, deptName);
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
