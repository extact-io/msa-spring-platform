package io.extact.msa.spring.platform.fw.integ;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.extact.msa.spring.platform.core.env.EnvConfig;
import io.extact.msa.spring.platform.core.log.LogConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote.RemotePersonRepositoryConfig;
import io.extact.msa.spring.platform.fw.stub.remote.RemotePersonStubController;
import io.extact.msa.spring.test.spring.NopTransactionManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("remote")
class RemoteApplicationIntegrationTest extends AbstractApplicationIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import({
        AbstractApplicationIntegrationTest.TestConfig.class,
        RemotePersonRepositoryConfig.class })
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        PlatformTransactionManager nopTransactionManager() {
            return new NopTransactionManager();
        }

        @Configuration(proxyBeanMethods = false)
        @Import({ LogConfig.class, EnvConfig.class, RestControllerConfig.class })
        class PersonStubControllerConfiguration {
            @Bean
            RemotePersonStubController remotePersonStubController() {
                return new RemotePersonStubController();
            }
        }
    }

    @Override
    protected int newDataId() {
        return 5;
    }
}
