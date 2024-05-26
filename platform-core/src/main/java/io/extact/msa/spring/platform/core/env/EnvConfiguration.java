package io.extact.msa.spring.platform.core.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import io.extact.msa.spring.platform.core.CoreConfiguration;

//ProjectInfoAutoConfiguration is required to activate BuildProperies, GitProperties
@Configuration(proxyBeanMethods = false)
@Import({ CoreConfiguration.class, ProjectInfoAutoConfiguration.class })
public class EnvConfiguration {

    @Bean
    MainModuleInformation mainJarInformation(Environment environment,
            @Autowired(required = false) BuildProperties buildProperties,
            @Autowired(required = false) GitProperties gitProperties) {
        return new MainModuleInformation(environment, buildProperties, gitProperties);
    }
}
