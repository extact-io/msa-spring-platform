package io.extact.msa.spring.platform.fw.stub.application.server.model;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.GenericRepository;
import lombok.NonNull;

public interface PersonRepository extends GenericRepository<Person> {

    Optional<Person> findName(@NonNull String name);
}
