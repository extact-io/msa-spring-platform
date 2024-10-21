package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;

// TODO:もしかしたら不要になるかもと思ったがSpringDataJpaExecutorは@NoRepositoryBeanなのでダメなような気がする
public interface EmployeeSpringDataJpa extends SpringDataJpaExecutor<EmployeeEntity>, EntityManagerHolder {

}
