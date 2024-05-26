package io.extact.msa.spring.platform.core.env;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

class MainModuleInformationTest {


    @Import(EnvConfiguration.class)
    static class TestConfig {
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "spring.application.name=platform-core")
    @TestPropertySource(properties = "spring.info.build.location=META-INF/build-info.properties")
    @TestPropertySource(properties = "spring.info.git.location=META-INF/git.properties")
    @Nested
    class NormalTest {

        @Test
        void test(@Autowired MainModuleInformation moduleInfo) {
            assertThat(moduleInfo.moduleInfo()).isEqualTo("platform-core:spring-sandbox.jar");
            assertThat(moduleInfo.versionInfo()).isEqualTo("0.0.1-SNAPSHOT:69f2859");
            assertThat(moduleInfo.buildTIme()).isEqualTo("2024/05/12 12:09:10");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "spring.info.build.location=none")
    @TestPropertySource(properties = "spring.info.git.location=none")
    @Nested
    class NoConfigTest {

        @Test
        void test(@Autowired MainModuleInformation moduleInfo) {
            assertThat(moduleInfo.moduleInfo()).isEqualTo("-:-");
            assertThat(moduleInfo.versionInfo()).isEqualTo("-:-");
            assertThat(moduleInfo.buildTIme()).isEqualTo("-");
        }
    }
}
