package io.extact.msa.spring.platform.core;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import io.extact.msa.spring.platform.core.async.AsyncConfig;
import io.extact.msa.spring.platform.core.async.AsyncInvoker;

@TestPropertySource(properties = """
        rms.async.enable=true
        """)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ConfigConditionalTest {

    @Configuration(proxyBeanMethods = false)
    @Import(AsyncConfig.class)
    static class TestConfig {

    }

    @Test
    void test(@Autowired(required = false) AsyncInvoker invoker) {
        assertThat(invoker).isNotNull();
    }
}
