package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeRepository;

public class EmployeeJpaRepository extends AbstractJpaRepository<Employee, EmployeeEntity>
        implements EmployeeRepository {

    public EmployeeJpaRepository(EmployeeSpringDataJpa executor,
            ModelEntityMapper<Employee, EmployeeEntity> entityMapper) {
        super(executor, entityMapper);
    }
}
