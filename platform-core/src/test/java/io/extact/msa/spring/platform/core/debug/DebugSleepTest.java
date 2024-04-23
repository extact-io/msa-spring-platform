package io.extact.msa.spring.platform.core.debug;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StopWatch;

import io.extact.msa.spring.platform.core.debug.DebugSleepInterceptor.DebugSleep;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = "rms.debug.sleep.enable=true")
class DebugSleepTest {

    @Value("${rms.debug.sleep.time}")
    private Duration sleepTime;

    @Configuration
    @Import(DebugConfiguration.class)
    static class TesConfig {
        @Bean
        TestTargetToClass testTargetToClass() {
            return new TestTargetToClass();
        }
        @Bean
        TestTargetToMethod testTargetToMethod() {
            return new TestTargetToMethod();
        }
    }

    @Test
    void testToClass(@Autowired TestTargetToClass target) {

        StopWatch timer = new StopWatch();
        timer.start();

        // sleepの実行
        target.execute();

        timer.stop();

        long duration = timer.getTotalTimeMillis();
        assertThat(duration).isGreaterThanOrEqualTo(sleepTime.toMillis());
    }

    @Test
    void testToMethod(@Autowired TestTargetToMethod target) {

        StopWatch timer = new StopWatch();
        timer.start();

        // sleepの実行
        target.execute();

        timer.stop();

        long duration = timer.getTotalTimeMillis();
        assertThat(duration).isGreaterThanOrEqualTo(sleepTime.toMillis());
    }


    @DebugSleep
    static class TestTargetToClass {
       void execute() {
           System.out.println("called TestTargetAtClass#execute");
       }
    }

    static class TestTargetToMethod {
        @DebugSleep
       void execute() {
           System.out.println("called TestTargetAtMethod#execute");
       }
    }

}
