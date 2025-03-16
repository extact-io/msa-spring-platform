package io.extact.msa.spring.platform.fw.stub.apps.employee.domain;

import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;

public interface EmployeeRepository extends GenericRepository<Employee>, DuplicationDataFinder<Employee>, IdentityGenerator {
}
