package io.extact.msa.spring.platform.fw.infrastructure.persistence;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.method.MethodValidationException;

import io.extact.msa.spring.platform.fw.stub.client.person.infrastructure.UpdateTestPersonRequest;
import io.extact.msa.spring.platform.fw.stub.server.person.infrastrucure.jpa.PersonJpaRepositoryConfig;
import io.extact.msa.spring.platform.fw.testapp.client.infrastructure.AddTestPersonRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("jpa")
class JpaApplicationIntegrationTest extends AbstractApplicationIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import({ AbstractApplicationIntegrationTest.TestConfig.class, PersonJpaRepositoryConfig.class })
    static class TestConfig {
    }

    @Override
    protected int newDataId() {
        return 1000;
    }

    @Test
    @Order(99)
    void testValidateResponseErrorWithAdd() {
        Throwable thrown = catchThrowable(() -> client.add(new AddTestPersonRequest("error")));
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }

    @Test
    @Order(99)
    void testValidateResponseErrorWithUpdate() {
        Throwable thrown = catchThrowable(() -> client.update(new UpdateTestPersonRequest(1, "error")));
        assertThat(thrown).isInstanceOf(MethodValidationException.class);
    }
}
