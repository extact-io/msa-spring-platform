package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import io.extact.msa.spring.platform.core.message.CustomLocalValidatorFactoryBean;

/**
 * BeanValidationのコンフィグレーション。
 * BeanValidationで利用するMessageSourceのBeanはspring.messageなどで利用者側で登録すること。
 */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(MessageSourceAutoConfiguration.class)
public class ValidatorConfig {

    /**
     * Spring配下のメッセージファイル(application-messages.proeperiesなど)をBeanValidationから
     * 利用可能にする。このBeanを登録した場合、BeanValidationのエラーメッセージは
     * ・Springのメッセージファイル
     * ・BeanValidaiton標準メッセージファイル、
     * の優先度で解決される。<br>
     * なお、MessageSourceのbean登録は利用者側で行うこと。
     *
     * @param messageSource メッセージソース。Bean名は"messageSource"であること
     * @return LocalValidatorFactoryBeanインスタンス
     */
    @Bean
    LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {
        LocalValidatorFactoryBean localValidatorFactoryBean = new CustomLocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    /**
     * メソッドバリデーションを有効にする。
     *
     * @return validationPostProcessor
     */
    @Bean
    MethodValidationPostProcessor validationPostProcessor(LocalValidatorFactoryBean validator) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setAdaptConstraintViolations(true);
        processor.setValidator(validator);
        return processor;
    }

    @Bean
    ValidationErrorTranslator validationErrorTranslator(MessageSource source) {
        return new ValidationErrorTranslator(source);
    }
}
