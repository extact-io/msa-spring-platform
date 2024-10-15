package io.extact.msa.spring.platform.fw.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RestControllerConfig {

    // デフォルト有効化でenable=falseが設定された場合のみ無効化
    @Bean
    @ConditionalOnProperty(prefix = "rms.fw.exceptionhanlder", name = "enable", havingValue = "true", matchIfMissing = true)
    RestControllerExceptionHandler restControllerExceptionHandler() {
        return new RestControllerExceptionHandler();
    }

    // デフォルト有効化でenable=falseが設定された場合のみ無効化
    @Bean
    @ConditionalOnProperty(prefix = "rms.fw.stopcontroller", name = "enable", havingValue = "true", matchIfMissing = true)
    ApplicationStopController applicationStopController() {
        return new ApplicationStopController();
    }
}
