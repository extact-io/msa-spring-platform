package io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.JpaRepositoryDelegator;

public interface PersonSpringDataJpa extends JpaRepositoryDelegator<PersonEntity> {

    Optional<PersonEntity> findByName(String name);
}
