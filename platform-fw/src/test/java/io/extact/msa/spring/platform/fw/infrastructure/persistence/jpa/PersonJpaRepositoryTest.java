package io.extact.msa.spring.platform.fw.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.infrastructure.framework.sqlinit.ProfileBasedDbInitializerConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastructure.jpa.PersonJpaRepositoryConfig;

@DataJpaTest
@ActiveProfiles("person-jpa")
class PersonJpaRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    private PersonRepository repository;

    @Configuration(proxyBeanMethods = false)
    @Import({
            ProfileBasedDbInitializerConfig.class,
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
        int firstTime = repository.nextIdentity();
        int secondTime = repository.nextIdentity();
        int thirdTime = repository.nextIdentity();
        // then
        assertThat(secondTime).isEqualTo(firstTime + 1);
        assertThat(thirdTime).isEqualTo(secondTime + 1);
    }
}
