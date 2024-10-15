package io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaInnerRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Employee;

public interface EmployeeJpaInnerRepository extends SpringDataJpaInnerRepository<Employee>, EntityManagerHolder {

}
