package io.extact.msa.spring.platform.fw.stub.application.server.persistence.file;

import io.extact.msa.spring.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.persistence.file.EntityArrayMapper;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.EmployeeRepository;

public class EmployeeFileRepository extends AbstractFileRepository<Employee> implements EmployeeRepository {

    static final String FILE_ENTITY = "employee";

    public EmployeeFileRepository(FileOperator fileReadWriter, EntityArrayMapper<Employee> converter) {
        super(fileReadWriter, converter);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }
}
