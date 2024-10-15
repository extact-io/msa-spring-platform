package io.extact.msa.spring.platform.fw.it;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.AddPersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.PersonClientResponse;
import io.extact.msa.spring.platform.fw.stub.application.client.external.dto.UpdatePersonClientRequest;
import io.extact.msa.spring.platform.fw.stub.application.server.persistence.jpa.PersonJpaRepositoryConfig;

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
        Throwable thrown = catchThrowable(() -> {
            PersonClientResponse res = client.add(new AddPersonClientRequest("error"));
            System.out.println(res);
        });
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    @Order(99)
    void testValidateResponseErrorWithUpdate() {
        Throwable thrown = catchThrowable(() -> client.update(new UpdatePersonClientRequest(1, "error")));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }
}
