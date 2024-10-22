package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaExecutor;

public interface PersonSpringDataJpa extends SpringDataJpaExecutor<PersonEntity> {

    Optional<PersonEntity> findByName(String name);
}
