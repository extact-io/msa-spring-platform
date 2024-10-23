package io.extact.msa.spring.platform.fw.stub.server.person.domain;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.NonNull;

public interface PersonRepository extends GenericRepository<Person> {

    Optional<Person> findName(@NonNull String name);
}
