package io.extact.msa.spring.platform.fw.stub.client.person.domain;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.model.ModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.DefaultModelPropertySupportFactory;
import io.extact.msa.spring.platform.fw.infrastructure.framework.model.ModelConfig;

@TestConfiguration(proxyBeanMethods = false)
@Import(ModelConfig.class)
public class ExternalPersonDomainConfig {

    @Bean
    ExternalPersonCreator externalPersonCreator(ModelValidator validator) {
        return new ExternalPersonCreator(validator, modelSupportFactory(validator));
    }

    private ModelPropertySupportFactory modelSupportFactory(ModelValidator validator) {
        return new DefaultModelPropertySupportFactory(validator);
    }
}
