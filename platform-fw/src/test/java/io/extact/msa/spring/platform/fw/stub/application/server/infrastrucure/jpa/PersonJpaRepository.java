package io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa;

import java.util.Optional;

import io.extact.msa.spring.platform.fw.persistence.jpa.AbstractJpaRepository;
import io.extact.msa.spring.platform.fw.persistence.jpa.ModelEntityMapper;
import io.extact.msa.spring.platform.fw.stub.application.server.model.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;
import lombok.NonNull;

public class PersonJpaRepository extends AbstractJpaRepository<Person, PersonEntity>
        implements PersonRepository {

    public PersonJpaRepository(PersonJpaExecutor executor, ModelEntityMapper<Person, PersonEntity> entityMapper) {
        super(executor, entityMapper);
    }

    @Override
    public void add(Person person) {
        if (isErrorPattern(person)) {
            person.changeName("1234567890"); // 桁数オーバーを起こさせる
        }
        super.add(person);
    }

    @Override
    public Optional<Person> update(Person person) {
        if (isErrorPattern(person)) {
            person.changeName("1234567890"); // 桁数オーバーを起こさせる
        }
        return super.update(person);
    }

    private boolean isErrorPattern(Person person) {
        return person.getName().equals("error");
    }

    @Override
    public Optional<Person> findName(@NonNull String name) {
        // TODO 自動生成されたメソッド・スタブ
        return Optional.empty();
    }
}
