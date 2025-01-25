package io.extact.msa.spring.platform.fw.infrastructure.framework.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.SpringModelValidatorAdapter;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidationErrorTranslator;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidatorConfig;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class ModelConfig {

    @Bean
    ModelValidator modelValidator(SmartValidator validator, ValidationErrorTranslator translator) {
        return new SpringModelValidatorAdapter(validator, translator);
    }
}
