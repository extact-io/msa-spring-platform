package io.extact.msa.spring.platform.core.env;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class EnvConfiguration {

    @Bean
    MainJarInformation mainJarInformation(Environment environment) {
        return new MainJarInformationFactory(environment).create();
    }
}
