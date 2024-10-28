package io.extact.msa.spring.platform.fw.stub.server.person.infrastrucure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.infrastructure.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.Person;
import lombok.NonNull;

public class PersonJpaRepository extends AbstractJpaRepository<Person, PersonEntity>
        implements PersonRepository {

    private PersonSpringDataJpa springJpa;
    private ModelEntityMapper<Person, PersonEntity> entityMapper;

    public PersonJpaRepository(PersonSpringDataJpa jpa, ModelEntityMapper<Person, PersonEntity> entityMapper) {
        super(jpa, entityMapper);
        this.springJpa = jpa;
        this.entityMapper = entityMapper;
    }

    @Override
    public Optional<Person> findName(@NonNull String name) {
        return springJpa.findByName(name)
                .map(entityMapper::toModel);
    }

    // ------ for test

    @Override
    public void add(Person person) {
        if (isErrorPattern(person)) {
            person.editName("1234567890"); // 桁数オーバーを起こさせる
        }
        super.add(person);
    }

    @Override
    public void update(Person person) {
        if (isErrorPattern(person)) {
            person.editName("1234567890"); // 桁数オーバーを起こさせる
        }
        super.update(person);
    }

    private boolean isErrorPattern(Person person) {
        return person.getName().equals("error");
    }

}
