package io.extact.msa.spring.platform.fw.stub.client.person.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;

@TestConfiguration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class ExternalPersonDomainConfig {

    @Bean
    ExternalPersonCreator externalPersonCreator(ModelValidator validator) {
        return new ExternalPersonCreator(validator);
    }
}
