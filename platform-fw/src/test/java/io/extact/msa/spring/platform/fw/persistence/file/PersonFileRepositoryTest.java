package io.extact.msa.spring.platform.fw.persistence.file;

import static io.extact.msa.spring.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import io.extact.msa.spring.platform.fw.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.application.server.domain.Person;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.file.PersonFileRepositoryConfig;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestExecutionListeners(listeners = { // 親クラスで定義したトランザクションが開始されないように必要なListenerだけ定義
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
@ActiveProfiles("file")
class PersonFileRepositoryTest extends AbstractPersonRepositoryTest {

    private PersonRepository repository;

    @Configuration(proxyBeanMethods = false)
    @Import(PersonFileRepositoryConfig.class)
    static class TestConfig {
    }

    // prototypeスコープにして毎回ファイルの初期が行われるようにする
    @BeforeEach
    void beforeEach(@Autowired @Qualifier("prototype") PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }

    @Test
    @Override
    protected void testAddToSpecificImplementation() {
        Person expected = Person.valueOf(5, "ADD");
        repository.add(Person.valueOf(null, "ADD"));
        assertThatToString(repository().get(5).get()).isEqualTo(expected);
    }

    @Test
    @Override
    protected void testDeleteToSpecificImplementation() {
        Person deleted = Person.valueOf(1, "dummy");
        repository.delete(deleted);
        assertThat(repository().get(1)).isNotPresent();
    }

    @Test
    @Override
    protected void testDeleteOnNotFoundToSpecificImplementation() {
        repository().delete(Person.valueOf(5, "dummy"));
    }
}
