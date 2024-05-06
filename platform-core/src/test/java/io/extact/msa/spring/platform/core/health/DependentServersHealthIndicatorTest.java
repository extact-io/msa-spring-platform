package io.extact.msa.spring.platform.core.health;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import io.extact.msa.spring.platform.core.health.client.ProbeResult;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClient;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DependentServersHealthIndicatorに対するテスト。
 * ReadinessProbeRestClientのテストは別の観点で実施している。
 */
class DependentServersHealthIndicatorTest {

    private static final String TEST_ENDPOINT = "http://localhost:%s/actuator/health/dependentServers";

    @Configuration(proxyBeanMethods = false)
    @SpringBootApplication
    @Import(HealthConfiguration.class)
    private static class TestConfig {

        @Bean
        @Primary
        ReadinessProbeRestClientFactory readinessProbeRestClientFactoryStub() {
            return new ReadinessProbeRestClientFactoryStub();
        }
    }


    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.health.depend-services[0]=http://localhost:8001/ok")
    @TestPropertySource(properties = "rms.health.depend-services[1]=http://localhost:8002/ok")
    @Nested
    class UpResultTest {

        private RestClient testClient;

        @BeforeEach
        void beforeEach(@Value("${local.server.port}") int port) {
            testClient = RestClient.builder()
                    .baseUrl(TEST_ENDPOINT.formatted(port))
                    .build();
        }

        @Test
        void test() {

            DependentServersHealthResponse expected = new DependentServersHealthResponse();
            expected.setStatus(Status.UP.getCode());

            Map<String, String> expectedDetail = Map.of(
                    "http://localhost:8001/ok", Status.UP.getCode(),
                    "http://localhost:8002/ok", Status.UP.getCode());
            expected.setDetails(expectedDetail);

            DependentServersHealthResponse actual = testClient
                    .get()
                    .retrieve()
                    .body(DependentServersHealthResponse.class);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    static class DependentServersHealthResponse {
        private String status;
        private Map<String, String> details;
    }

    static class ReadinessProbeRestClientFactoryStub implements ReadinessProbeRestClientFactory {

        @Override
        public ReadinessProbeRestClient create() {
            return new ReadinessProbeRestClientStub();
        }
    }

    static class ReadinessProbeRestClientStub implements ReadinessProbeRestClient {

        @Override
        public ProbeResult probeReadiness(String url) {
            return switch (url) {
                case String s when s.endsWith("ok") -> new ProbeResult(url, Status.UP);
                case String s when s.endsWith("ng") -> new ProbeResult(url, Status.DOWN);
                default -> throw new IllegalArgumentException("Unexpected value: " + url);
            };
        }

        @Override
        public CompletionStage<ProbeResult> probeReadinessAsync(String url) {
            return CompletableFuture.supplyAsync(() -> this.probeReadiness(url));
        }
    }
}
