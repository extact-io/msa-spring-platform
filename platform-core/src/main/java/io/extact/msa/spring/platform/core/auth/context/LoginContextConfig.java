package io.extact.msa.spring.platform.core.auth.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LoginContextConfig {
    @Bean
    LoginContext loginContext() {
        return new DefaultLoginContext();
    }
}
