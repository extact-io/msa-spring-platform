package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.infrastructure.datasource.ApplicationDataSourceConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.model.PersonId;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.jpa.PersonJpaRepositoryConfig;

@DataJpaTest
@ActiveProfiles("person-jpa")
class PersonJpaRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    private PersonRepository repository;

    @Configuration(proxyBeanMethods = false)
    @Import({
            ApplicationDataSourceConfig.class,
            PersonJpaRepositoryConfig.class })
    static class TestConfig {
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }

    @Test
    @Override
    protected void testNextIdentity() {
        // when
        PersonId firstTime = repository.nextIdentity();
        PersonId secondTime = repository.nextIdentity();
        PersonId thirdTime = repository.nextIdentity();
        // then
        assertThat(secondTime.id()).isEqualTo(firstTime.id() + 1);
        assertThat(thirdTime.id()).isEqualTo(secondTime.id() + 1);
    }
}
