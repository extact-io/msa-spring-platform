package io.extact.msa.spring.platform.fw.stub.server.person.infrastrucure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.SpringDataJpaExecutor;

public interface PersonSpringDataJpa extends SpringDataJpaExecutor<PersonEntity> {

    Optional<PersonEntity> findByName(String name);
}
