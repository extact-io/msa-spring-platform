package io.extact.msa.spring.platform.fw.stub.apps.employee.domain;

import io.extact.msa.spring.platform.fw.domain.repository.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.repository.IdProvider;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.Employee;
import io.extact.msa.spring.platform.fw.stub.apps.employee.domain.model.EmployeeId;

public interface EmployeeRepository
        extends GenericRepository<Employee>, DuplicationDataFinder<Employee>, IdProvider<EmployeeId> {
}
