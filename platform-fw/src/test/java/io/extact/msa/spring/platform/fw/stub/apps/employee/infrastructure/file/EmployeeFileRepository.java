package io.extact.msa.spring.platform.fw.stub.apps.employee.infrastructure.file;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.AbstractFileRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.ModelArrayMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.file.io.FileOperator;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.EmployeeRepository;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;

public class EmployeeFileRepository extends AbstractFileRepository<Employee> implements EmployeeRepository {

    static final String FILE_ENTITY = "employee";

    public EmployeeFileRepository(FileOperator fileReadWriter, ModelArrayMapper<Employee> mapper) {
        super(fileReadWriter, mapper);
    }

    @Override
    public String getEntityName() {
        return FILE_ENTITY;
    }

    @Override
    public Optional<Employee> findDuplicationData(Employee checkModel) {
        return this.findAll().stream()
                .filter(employee -> employee.getName().equals(checkModel.getName()))
                .findFirst();
    }
}
