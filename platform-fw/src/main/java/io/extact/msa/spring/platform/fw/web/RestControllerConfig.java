package io.extact.msa.spring.platform.fw.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidationErrorTranslator;

@Configuration(proxyBeanMethods = false)
public class RestControllerConfig {

    // デフォルト有効化でenable=falseが設定された場合のみ無効化
    @Bean
    @ConditionalOnProperty(prefix = "rms.fw.exceptionhanlder", name = "enable", havingValue = "true", matchIfMissing = true)
    RestControllerExceptionHandler restControllerExceptionHandler(ValidationErrorTranslator translator) {
        return new RestControllerExceptionHandler(translator);
    }

    // デフォルト有効化でenable=falseが設定された場合のみ無効化
    @Bean
    @ConditionalOnProperty(prefix = "rms.fw.stopcontroller", name = "enable", havingValue = "true", matchIfMissing = true)
    ApplicationStopController applicationStopController() {
        return new ApplicationStopController();
    }
}
