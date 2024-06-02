package io.extact.msa.spring.platform.fw.domain.constraint;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import io.extact.msa.spring.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.spring.test.assertj.ConstraintViolationSetAssert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class BeforeAfterDateTimeTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:application-messages");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;

//            StaticMessageSource messageSource = new StaticMessageSource();
//            Locale locale = Locale.getDefault();
//            messageSource.addMessage("local.BeforeAfterDateTime", locale, "{0}と{1}の過去は振り返らない！");
//            messageSource.addMessage("BeforeAfterDateTime", locale, "{1}と{2}の過去の過去！");
//            messageSource.addMessage("from", locale, "開始");
//            messageSource.addMessage("to", locale, "終了");
//            return messageSource;
        }

        @Bean
        LocalValidatorFactoryBean localValidatorFactoryBean(MessageSource messageSource) {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            //localValidatorFactoryBean.setValidationMessageSource(messageSource);
            MessageInterpolatorFactory messageInterpolatorFactory = new MessageInterpolatorFactory(messageSource);
            localValidatorFactoryBean.setMessageInterpolator(messageInterpolatorFactory.getObject());
            localValidatorFactoryBean.afterPropertiesSet();

            return localValidatorFactoryBean;
        }
    }

    @Autowired
    MessageSource messageSource;

    @Test
    void testSpringValidate(@Autowired org.springframework.validation.Validator validator) {

//        var OK = new Data(LocalDateTime.now().minusHours(1), LocalDateTime.now());
//        Errors errors = validator.validateObject(OK);
//        assertThat(errors.hasErrors()).isFalse();

        var NG = new Data(LocalDateTime.now(), LocalDateTime.now().minusHours(1));
        Errors errors = validator.validateObject(NG);

        errors.getGlobalErrors().forEach(error -> {
            String message = messageSource.getMessage(error, Locale.getDefault());
            System.out.println(error);
            System.out.println(message);
        });

        assertThat(errors.hasErrors()).isTrue();
    }


    @Test
    void testValidateContact(@Autowired org.springframework.validation.Validator validator) {

        // 40文字より大きい
        var NG= new Data2("12345678901234567890123456789012345678901");

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(NG, "bean");

        validator.validate(NG, errors);
        System.out.println(errors);
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data2 {
        @Contact
        private String value;
    }


    @Test
    void testValidate(@Autowired Validator validator) {
        var OK = new Data(LocalDateTime.now().minusHours(1), LocalDateTime.now());
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
                .hasNoViolations();

        var NG = new Data(LocalDateTime.now(), LocalDateTime.now().minusHours(1));
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
                .hasSize(1)
                .hasMessageEndingWith("BeforeAfterDateTime.message");
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @BeforeAfterDateTime(from = "from", to = "to", message = "{local.BeforeAfterDateTime}")
    static class Data implements BeforeAfterDateTimeValidatable {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
