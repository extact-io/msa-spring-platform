package io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaInnerRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Employee;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeJpaRepository extends AbstractJpaRepository<Employee> implements EmployeeRepository {

    private final EmployeeJpaInnerRepository innerRepository;

    @Override
    public SpringDataJpaInnerRepository<Employee> innerRepository() {
        return innerRepository;
    }
}
