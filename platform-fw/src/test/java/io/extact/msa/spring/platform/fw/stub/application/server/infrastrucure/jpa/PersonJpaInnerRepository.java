package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;

public interface PersonJpaInnerRepository extends SpringDataJpaExecutor<Person>, EntityManagerHolder {

}
