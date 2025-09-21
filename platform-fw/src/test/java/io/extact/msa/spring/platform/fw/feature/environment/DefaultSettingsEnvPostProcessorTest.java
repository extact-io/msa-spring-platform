package io.extact.msa.spring.platform.fw.feature.environment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DefaultSettingsEnvPostProcessorTest {

    @Configuration(proxyBeanMethods = false)
    static class TestConfig {
    }

    @Test
    void testSample(@Autowired Environment env) {

        // given
        String propName = "sample.prop";

        // when
        String actual = env.getProperty(propName);

        // then
        //assertThat(actual).isEqualTo("abc");
    }
}
