package io.extact.msa.spring.platform.core.health;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.extact.msa.spring.platform.core.health.client.ProbeResult;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClient;
import io.extact.msa.spring.platform.core.health.client.ReadinessProbeRestClientFactory;

/**
 * ReadinessProbeRestClientに対するテスト。
 * <pre>
 * ・実物：ReadinessProbeRestClientFactoryImpl
 * ・実物：ReadinessProbeRestClientImpl
 *     ↓ HTTP(RANDOM)
 * ・スタブ：TestStubResource
 * </pre>
 * ReadinessProbeRestClientのテストは別の観点で実施している。
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReadinessProbeRestClientTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @Import(HealthConfiguration.class)
    static class TestConfig {

        @Bean
        TestStubResource testStubResource() {
            return new TestStubResource();
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests((requests) -> requests.anyRequest().anonymous());
            return http.build();
        }
    }

    private static String serverUrl;

    @BeforeAll
    static void beforeAll(@Value("${local.server.port}") int port) {
        serverUrl = "http://localhost:%s".formatted(port);
    }

    @Test
    void testUpRespose(@Autowired ReadinessProbeRestClientFactory factory) {

        String testEndpoint = serverUrl + "/ok";

        ProbeResult expected = new ProbeResult(testEndpoint, Status.UP);

        ReadinessProbeRestClient probeClient = factory.create();
        ProbeResult actual = probeClient.probeReadiness(testEndpoint);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testDownRespose(@Autowired ReadinessProbeRestClientFactory factory) {

        // --- return statuscode: 500
        String testEndpoint = serverUrl + "/down500";

        ProbeResult expected = new ProbeResult(testEndpoint, Status.DOWN);

        ReadinessProbeRestClient probeClient = factory.create();
        ProbeResult actual = probeClient.probeReadiness(testEndpoint);

        assertThat(actual).isEqualTo(expected);

        // --- return statuscode: 503
        testEndpoint = serverUrl + "/down503";

        expected = new ProbeResult(testEndpoint, Status.DOWN);

        actual = probeClient.probeReadiness(testEndpoint);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testUnknownRespose(@Autowired ReadinessProbeRestClientFactory factory) {

        String testEndpoint = serverUrl + "/unknown";

        ProbeResult expected = new ProbeResult(testEndpoint, Status.UNKNOWN);

        ReadinessProbeRestClient probeClient = factory.create();
        ProbeResult actual = probeClient.probeReadiness(testEndpoint);

        assertThat(actual).isEqualTo(expected);
    }

    @RestController
    static class TestStubResource {

        @GetMapping("/{param}")
        public ResponseEntity<String> ok(@PathVariable("param") String param) {
            return switch (param) {
                case "ok" -> ResponseEntity.ok("ok");
                case "down500" -> ResponseEntity.internalServerError().body("down500");
                case "down503" -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE.value()).body("down503");
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body("unknown");
            };
        }
    }
}
