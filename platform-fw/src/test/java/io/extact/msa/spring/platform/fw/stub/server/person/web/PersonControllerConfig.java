package io.extact.msa.spring.platform.fw.stub.server.person.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.stub.server.person.application.PersonService;

@TestConfiguration(proxyBeanMethods = false)
@Import(RestControllerConfig.class)
public class PersonControllerConfig {

    @Bean
    PersonController personController(PersonService service) {
        return new PersonController(service);
    }
}
