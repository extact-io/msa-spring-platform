package io.extact.msa.spring.platform.fw.infrastructure.persistence.file;

import org.junit.jupiter.api.BeforeEach;
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

import io.extact.msa.spring.platform.fw.infrastructure.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastrucure.file.PersonFileRepositoryConfig;

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
}
