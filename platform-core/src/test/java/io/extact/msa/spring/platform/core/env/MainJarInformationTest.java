package io.extact.msa.spring.platform.core.env;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

/*
 * !!NOTE!!
 * Eclipseでこのテストを実行する場合は/rms-platform/testdata/environment-test-normal.zipを
 * 外部jarとして追加する（mavenから実行する場合はpomに設定を入れているので特別な手順は不要）
 * また'*.jar'にすると.gitignoreでファイルをcommitできないので*.zipにしている
 */
//@EnabledIfSystemProperty(named = "mvn.cli.profile", matches = "on") // execute cli only
class MainJarInformationTest {

    @SpringBootTest(classes = EnvConfiguration.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "rms.env.main.jar=environment-test-normal\\.zip$")
    @Nested
    class NormalTest {
        @Test
        void test(@Autowired MainJarInformation jarInfo) {

            assertThat(jarInfo.getApplicationName()).isEqualTo("RentalManagementSystem");
            assertThat(jarInfo.getJarName()).isEqualTo("environment-test-normal.zip");
            assertThat(jarInfo.getMainClassName()).isEqualTo("dummy.Dummy");
            assertThat(jarInfo.getVersion()).isEqualTo("0.0.1-SNAPSHOT");
            assertThat(jarInfo.getBuildTimeInfo()).isNotNull();

            assertThat(jarInfo.startupModuleInfo())
                    .isEqualTo("RentalManagementSystem/environment-test-normal.zip/dummy.Dummy");
        }
    }

    @SpringBootTest(classes = EnvConfiguration.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "rms.env.main.jar=dummy\\.jar$")
    @Nested
    class MainJarInfoNotFoundTest {
        @Test
        void test(@Autowired MainJarInformation jarInfo) {
            assertThat(jarInfo).isSameAs(MainJarInformation.UNKNOWN);
        }
    }

    @SpringBootTest(classes = EnvConfiguration.class, webEnvironment = WebEnvironment.NONE)
    @Nested
    class MainJarInfoNoProperty {
        @Test
        void test(@Autowired MainJarInformation jarInfo) {
            assertThat(jarInfo).isSameAs(MainJarInformation.UNKNOWN);
        }
    }

    @SpringBootTest(classes = EnvConfiguration.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "jakarta")
    @Nested
    class MainJarInfoTooManyMatch {
        @Test
        void tetGetMainJarInfoTooManyMatch(@Autowired MainJarInformation jarInfo) {
            assertThat(jarInfo).isSameAs(MainJarInformation.UNKNOWN);
        }
    }

    @SpringBootTest(classes = EnvConfiguration.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "jakarta\\.inject-api")
    @Nested
    class MainJarInfoUnknownApplicationName {
        @Test
        void tetGetMainJarInfoUnknownApplicationName(@Autowired MainJarInformation jarInfo) {
            assertThat(jarInfo.startupModuleInfo()).isEqualTo("-");
        }
    }
}
