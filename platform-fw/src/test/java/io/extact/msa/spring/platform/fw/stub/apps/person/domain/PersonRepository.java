package io.extact.msa.spring.platform.fw.stub.apps.person.domain;

import io.extact.msa.spring.platform.fw.domain.repository.GenericRepository;
import io.extact.msa.spring.platform.fw.domain.service.DuplicationDataFinder;
import io.extact.msa.spring.platform.fw.domain.service.IdentityGenerator;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

public interface PersonRepository extends GenericRepository<Person>, DuplicationDataFinder<Person>, IdentityGenerator  {
}
