package io.extact.msa.spring.platform.fw.stub.apps.employee.infrastructure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.JpaRepositoryDelegator;

public interface EmployeeJpaRepositoryDelegator extends JpaRepositoryDelegator<EmployeeEntity> {

    Optional<EmployeeEntity> findByName(String name);
}
