package io.extact.msa.spring.platform.fw.stub.server.employee.infrastructure.file;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.EmployeeRepository;
import io.extact.msa.spring.platform.fw.stub.server.employee.domain.model.Employee;

public class EmployeeFileRepository extends AbstractFileRepository<Employee> implements EmployeeRepository {

    static final String FILE_ENTITY = "employee";

    public EmployeeFileRepository(FileOperator fileReadWriter, ModelArrayMapper<Employee> converter) {
        super(fileReadWriter, converter);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }
}
