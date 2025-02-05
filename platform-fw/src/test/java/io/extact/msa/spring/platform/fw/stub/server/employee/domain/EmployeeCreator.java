package io.extact.msa.spring.platform.fw.stub.server.employee.domain;

import io.extact.msa.spring.platform.fw.domain.model.ModelCreator;
import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.EmployeeCreator.EmployeeModelAttributes;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee.EmployeeCreatable;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.EmployeeId;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeCreator implements ModelCreator<Employee, EmployeeModelAttributes> {

    private final IdentityGenerator idGenerator;
    private final ModelValidator validator;
    private final ModelPropertySupportFactory modelSupportFactory;
    private final EmployeeCreatable constructorProxy = new EmployeeCreatable() {};

    public Employee create(EmployeeModelAttributes attrs) {

        EmployeeId id = new EmployeeId(idGenerator.nextIdentity());
        Employee employee = constructorProxy.newInstance(id, attrs.name, attrs.deptName);

        employee.configureSupport(modelSupportFactory);
        validator.validateModel(employee);

        return employee;
    }

    @Builder
    public static class EmployeeModelAttributes {
        private String name;
        private String deptName;
    }
}
