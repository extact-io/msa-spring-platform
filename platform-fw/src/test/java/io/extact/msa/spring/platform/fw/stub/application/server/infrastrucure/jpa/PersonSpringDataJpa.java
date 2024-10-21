package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.jpa.EntityManagerHolder;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;

public interface PersonSpringDataJpa extends SpringDataJpaExecutor<PersonEntity>, EntityManagerHolder {

    Optional<PersonEntity> findByName(String name);
}
