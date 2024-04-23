package io.extact.msa.spring.platform.core.debug;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = "rms.debug.configdump.enable=true")
class ConfigDumpTest {

    @Configuration
    @Import(DebugConfiguration.class)
    static class TesConfig {
    }

    @Test
    void test() {
        System.out.println("execute test");
    }
}
