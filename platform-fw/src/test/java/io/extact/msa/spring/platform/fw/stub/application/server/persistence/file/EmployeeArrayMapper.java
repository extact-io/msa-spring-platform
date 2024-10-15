package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.persistence.file.EntityArrayMapper;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Employee;

public class EmployeeArrayMapper implements EntityArrayMapper<Employee> {

    public static final EmployeeArrayMapper INSTANCE = new EmployeeArrayMapper();

    @Override
    public Employee toEntity(String[] attributes) throws RmsSystemException {
        Integer id = Integer.parseInt(attributes[0]);
        String name = attributes[1];
        String deptName = attributes[2];
        return Employee.valueOf(id, name, deptName);
    }

    @Override
    public String[] toArray(Employee employee) {
        String[] attributes = new String[3];
        attributes[0] = String.valueOf(employee.getId());
        attributes[1] = employee.getName();
        attributes[1] = employee.getDeptName();
        return attributes;
    }
}
