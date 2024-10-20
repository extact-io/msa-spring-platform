package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.file;

import io.extact.msa.spring.platform.fw.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeRepository;

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
