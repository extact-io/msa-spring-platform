package io.extact.msa.spring.platform.fw.feature.environment;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

class DefaultSettingsEnvPostProcessorTest {

    @Configuration(proxyBeanMethods = false)
    static class TestConfig {
    }

    // 上書きなし(application.yml)
    @Nested
    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    static class NormalTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("yyyyMMdd");
        }
    }

    // 上書きあり(application.yml)
    @Nested
    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    static class OverrideByApplicationTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("yyyyMMdd");
        }
    }

    // 上書きあり(application.yml→application-<profile>.yml)
    @Nested
    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    @ActiveProfiles("env-profile-test")
    static class OverrideByProfileTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("profile");
        }
    }

    // 上書きあり(application.yml→spring.config.import)
    @Nested
    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.NONE)
    @ActiveProfiles("env-import-test")
    static class OverrideByImportTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("import");
        }
    }

    // 上書きあり(application.yml→spring.config.additional-location)
    @Nested
    @SpringBootTest( //
            classes = TestConfig.class, //
            args = "--spring.config.additional-location=classpath:/env-test/application-additional.yml", //
            webEnvironment = WebEnvironment.NONE)
    static class OverrideByAdditionalTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("additional");
        }
    }

    // 上書きあり(application.yml→spring.config.import→application-<profile>.yml→spring.config.additional-location)
    @Nested
    @SpringBootTest( //
            classes = TestConfig.class, //
            args = "--spring.config.additional-location=classpath:/env-test/application-additional.yml", //
            webEnvironment = WebEnvironment.NONE)
    @ActiveProfiles({ "env-profile-test", "env-import-test" })
    static class OverrideByComplexTest {
        @Test
        void test(@Autowired Environment env) {
            // given
            String propName = "rms.rest.controller.format.date";
            // when
            String actual = env.getProperty(propName);
            // then
            assertThat(actual).isEqualTo("additional");
        }
    }
}
