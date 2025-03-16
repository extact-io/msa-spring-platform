package io.extact.msa.spring.platform.fw.integ;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.file.PersonFileRepositoryConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("file")
class FileApplicationIntegrationTest extends AbstractApplicationIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import({ AbstractApplicationIntegrationTest.TestConfig.class, PersonFileRepositoryConfig.class })
    static class TestConfig {
    }

    @Override
    protected int newDataId() {
        return 5;
    }
}
