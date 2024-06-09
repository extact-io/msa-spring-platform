package io.extact.msa.spring.platform.fw.domain.constraint;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.extact.msa.spring.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import jakarta.validation.constraints.Size;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ApplicationMessageTest {

    @Configuration(proxyBeanMethods = false)
    @Import(ValidationTestConfig.class)
    static class TestConfig {

        @Bean
        @Primary
        MessageSource messageSourceForTest() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasenames("classpath:application-messages", "classpath:test-application-messages");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        }
    }

    @Test
    void testDefaultMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        Data NG = new Data(LocalDateTime.now(), LocalDateTime.now().minusHours(1));

        Errors errors = validator.validateObject(NG);

        String defaultMessage = errors.getGlobalError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("{1}より過去の{2}は許可されていません");

        String resolvedMessage = messageSource.getMessage(errors.getGlobalError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("利用開始日時より過去の利用終了日時は許可されていません");
    }

    @Test
    void testCustomMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        Value val = new Value("12345");
        Errors errors = validator.validateObject(val);

        String defaultMessage = errors.getFieldError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("override default message, parameter=1,3");

        String resolvedMessage = messageSource.getMessage(errors.getFieldError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("名前は1から3のサイズにしてください");
    }

    @lombok.Data
    @BeforeAfterDateTime
    static class Data implements BeforeAfterDateTimeValidatable {
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
    }

    @lombok.Data
    static class Value {
        @Size(min = 1, max = 3)
        private final String name;
    }
}
