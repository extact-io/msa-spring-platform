package io.extact.msa.spring.platform.core.env;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class EnvConfigurationTest {

    private static final String MAIN_MANIFEST_JAR_PROP = "rms.env.main.jar";

    @AfterEach
    void teardown( ) {
        System.clearProperty(MAIN_MANIFEST_JAR_PROP);
        EnvConfiguration.clear();
    }

    @Test
    void tetGetMainJarInfo() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "environment-test-normal\\.jar$");

        MainJarInformation mainJarInfo1 = EnvConfiguration.getMainJarInfo();
        MainJarInformation mainJarInfo2 = EnvConfiguration.getMainJarInfo();

        assertThat(mainJarInfo1).isSameAs(mainJarInfo2);
    }

    @Test
    void testUnknowMainJarInfo() {

        // "main.manifest.jar" prop non.
        MainJarInformation mainJarInfo = EnvConfiguration.getMainJarInfo();

        assertThat(mainJarInfo.getApplicationName()).isEqualTo("-");
        assertThat(mainJarInfo.getJarName()).isEqualTo("-");
        assertThat(mainJarInfo.getMainClassName()).isEqualTo("-");
        assertThat(mainJarInfo.getVersion()).isEqualTo("-");
        assertThat(mainJarInfo.getBuildtimeInfo()).isEqualTo("-");
    }
}
