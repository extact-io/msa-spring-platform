package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.model.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeJpaRepository extends AbstractJpaRepository<Employee> implements EmployeeRepository {

    private final EmployeeJpaInnerRepository innerRepository;

    @Override
    public SpringDataJpaExecutor<Employee> executor() {
        return innerRepository;
    }
}
