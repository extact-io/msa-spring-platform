package io.extact.msa.spring.platform.core.debug;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ConfigDumpTest {

    @Configuration
    static class TesConfig {

        @Bean
        ConfigDump configDump(Environment env) {
            return new ConfigDump(env);
        }
    }

    @Test
    void test() {
        System.out.println("execute test");
    }
}
