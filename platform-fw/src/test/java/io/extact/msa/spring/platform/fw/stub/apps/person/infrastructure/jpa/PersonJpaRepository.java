package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.Person;

public class PersonJpaRepository extends AbstractJpaRepository<Person, PersonEntity>
        implements PersonRepository {

    private PersonJpaRepositoryDelegator springJpa;
    private ModelEntityMapper<Person, PersonEntity> entityMapper;

    public PersonJpaRepository(PersonJpaRepositoryDelegator jpa, ModelEntityMapper<Person, PersonEntity> entityMapper) {
        super(jpa, entityMapper);
        this.springJpa = jpa;
        this.entityMapper = entityMapper;
    }

    @Override
    public Optional<Person> findDuplicationData(Person checkPerson) {
        return springJpa.findByName(checkPerson.getName())
                .map(entityMapper::toModel);
    }

    // ------ for test

    @Override
    public void add(Person person) {
        if (isErrorPattern(person)) {
            person.editNameWithoutValidation("1234567890"); // 桁数オーバーを起こさせる
        }
        super.add(person);
    }

    @Override
    public void update(Person person) {
        if (isErrorPattern(person)) {
            person.editNameWithoutValidation("1234567890"); // 桁数オーバーを起こさせる
        }
        super.update(person);
    }

    private boolean isErrorPattern(Person person) {
        return person.getName().equals("error");
    }
}
