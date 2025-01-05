package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfig;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;

@Configuration(proxyBeanMethods = false)
@Import(ValidationConfig.class)
public class ValidatorConfig {

    @Bean
    ValidationErrorTranslator validationErrorTranslator(MessageSource source) {
        return new ValidationErrorTranslator(source);
    }

    @Bean
    ModelValidator modelValidator(SmartValidator validator, ValidationErrorTranslator translator) {
        return new SpringModelValidatorAdapter(validator, translator);
    }
}
