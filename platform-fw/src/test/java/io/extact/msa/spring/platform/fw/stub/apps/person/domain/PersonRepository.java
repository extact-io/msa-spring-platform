package io.extact.msa.spring.platform.fw.stub.apps.person.domain;

import io.extact.msa.spring.platform.fw.domain.repository.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.repository.IdProvider;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;

public interface PersonRepository
        extends GenericRepository<Person>, DuplicationDataFinder<Person>, IdProvider<PersonId> {
}
