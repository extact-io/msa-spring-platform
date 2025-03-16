package io.extact.msa.spring.platform.fw.stub.apps.person.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.stub.apps.person.application.PersonService;

@Configuration(proxyBeanMethods = false)
@Import(RestControllerConfig.class)
public class PersonControllerConfig {

    @Bean
    PersonController personController(PersonService service) {
        return new PersonController(service);
    }
}
