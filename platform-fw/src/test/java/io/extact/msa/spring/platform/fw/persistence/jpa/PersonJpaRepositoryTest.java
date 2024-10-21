package io.extact.msa.spring.platform.fw.persistence.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.application.server.infrastrucure.jpa.PersonJpaRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.application.server.model.PersonRepository;

@DataJpaTest
@ActiveProfiles("jpa")
class PersonJpaRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    private PersonRepository repository;

    @Configuration(proxyBeanMethods = false)
    @Import(PersonJpaRepositoryConfig.class)
    static class TestConfig {
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }
}
