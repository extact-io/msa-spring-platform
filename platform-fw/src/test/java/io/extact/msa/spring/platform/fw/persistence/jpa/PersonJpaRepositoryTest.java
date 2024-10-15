package io.extact.msa.spring.platform.fw.persistence.jpa;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa.PersonJpaInnerRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa.PersonJpaRepositoryConfig;

@DataJpaTest
@ActiveProfiles("jpa")
class PersonJpaRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    private PersonRepository repository;
    @Autowired
    private PersonJpaInnerRepository inner; // for eneityManager access.

    @Configuration(proxyBeanMethods = false)
    @Import(PersonJpaRepositoryConfig.class)
    static class TestConfig {
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }

    @Test
    @Override
    protected void testAddToSpecificImplementation() {
        Person addPerson = Person.valueOf(null, "ADD");
        repository.add(addPerson);
        assertThat(inner.isManaged(addPerson)).isTrue(); // managed state check?
        assertThat(addPerson.getId()).isEqualTo(1001);
    }

    @Test
    @Override
    protected void testDeleteToSpecificImplementation() {

        Optional<Person> delete = repository.get(1);
        assertThat(delete).isPresent();

        delete.ifPresent(person -> repository.delete(person));
        Optional<Person> deleted = repository.get(1);
        assertThat(deleted).isNotPresent();
    }

    @Test
    @Override
    protected void testDeleteOnNotFoundToSpecificImplementation() {
        // JPA実装では発生し得ないためテストはなし
    }
}
