package io.extact.msa.spring.platform.core.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AsyncConfig {

    @Bean
    AsyncInvoker asyncInvoker() {
        return new AsyncInvoker();
    }
}
