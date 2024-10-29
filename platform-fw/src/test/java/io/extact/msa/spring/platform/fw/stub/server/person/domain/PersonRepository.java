package io.extact.msa.spring.platform.fw.stub.server.person.domain;

import io.extact.msa.spring.platform.fw.domain.service.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.GenericRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;

public interface PersonRepository extends GenericRepository<Person>, DuplicationDataFinder<Person> {
}
