package io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.SpringDataJpaInnerRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.PersonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonJpaRepository extends AbstractJpaRepository<Person> implements PersonRepository {

    private final PersonJpaInnerRepository innerRepository;

    @Override
    public SpringDataJpaInnerRepository<Person> innerRepository() {
        return innerRepository;
    }

    @Override
    public void add(Person entity) {
        if (isErrorPattern(entity)) {
            entity.setName("1234567890"); // 桁数オーバーを起こさせる
        }
        super.add(entity);
    }

    @Override
    public Optional<Person> update(Person entity) {
        if (isErrorPattern(entity)) {
            entity.setName("1234567890"); // 桁数オーバーを起こさせる
        }
        return super.update(entity);
    }

    private boolean isErrorPattern(Person entity) {
        return entity.getName().equals("error");
    }
}
