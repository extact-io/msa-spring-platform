package io.extact.msa.spring.platform.fw.domain.constraint;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;

import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.extact.msa.spring.platform.fw.domain.constraint.EqualPairFields.EqualPairFieldsValidatable;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ApplicationMessageTest {

    @Configuration(proxyBeanMethods = false)
    @Import(ValidationConfiguration.class)
    static class TestConfig {
        // NOP
    }

    @Test
    void testDefaultMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        ParamDto NG = new ParamDto("value1", "value2");

        Errors errors = validator.validateObject(NG);

        String defaultMessage = errors.getGlobalError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("{1}と{2}を同じ値にしてください");

        String resolvedMessage = messageSource.getMessage(errors.getGlobalError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("テスト値1とテスト値2を同じ値にしてください");
    }

    @Test
    void testCustomMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        Value val = new Value("12345");
        Errors errors = validator.validateObject(val);

        String defaultMessage = errors.getFieldError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("override default message, parameter=1,3");

        // MessageSourceでメッセージを上書き
        String resolvedMessage = messageSource.getMessage(errors.getFieldError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("名前は1から3のサイズにしてください");
    }

    @EqualPairFields
    static record ParamDto(
            @Size(min = 2) String val1,
            @Size(min = 2) String val2) implements EqualPairFieldsValidatable {
    }

    static record Value(
            @Size(min = 1, max = 3) //
            String name) {
    }
}
