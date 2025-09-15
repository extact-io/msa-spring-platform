package io.extact.msa.spring.platform.fw.infrastructure.persistence.remote;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.extact.msa.spring.platform.core.env.EnvConfig;
import io.extact.msa.spring.platform.core.log.LogConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.AbstractPersonRepositoryTest;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.domain.PersonRepository;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePersonRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.remote.RemotePersonStubController;
import io.extact.msa.spring.test.spring.EnableAutoConfigurationWithoutSecurityAndActuator;
import io.extact.msa.spring.test.spring.NopTransactionManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfigurationWithoutSecurityAndActuator // 認証とactuatorを除外
@ActiveProfiles("person-remote")
class RemotePersonRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    private PersonRepository repository;
    @Autowired
    private TestRestTemplate testRestTemplate; // for reset

    @Configuration(proxyBeanMethods = false)
    @Import({
            RemotePersonRepositoryConfig.class,
            // for RemoteStubController Config
            LogConfig.class,
            EnvConfig.class,
            RestControllerConfig.class
    })
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        RemotePersonStubController remotePersonStubController() {
            return new RemotePersonStubController();
        }

        @Bean
        PlatformTransactionManager nopTransactionManager() {
            return new NopTransactionManager();
        }
    }

    @BeforeEach
    void beforeEach() {
        testRestTemplate.getForEntity("/remote-persons/reset", Void.class);
    }

    @Override
    protected PersonRepository repository() {
        return repository;
    }

    @Test
    @Override
    protected void testNextIdentity() {
        // given
        int currentId = repository.findAll().size();
        // when
        int nextId = repository.nextIdentity();
        // then
        assertThat(nextId).isEqualTo(currentId + 1);
    }
}
