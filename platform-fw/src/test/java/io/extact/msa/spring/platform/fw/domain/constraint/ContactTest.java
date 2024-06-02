package io.extact.msa.spring.platform.fw.domain.constraint;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import io.extact.msa.spring.test.assertj.ConstraintViolationSetAssert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SpringBootTest(classes = LocalValidatorFactoryBean.class, webEnvironment = WebEnvironment.NONE)
class ContactTest {

    @Test
    void testValidate(@Autowired Validator validator) {
        var OK = new Data("東京都新宿区");
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 40文字より大きい
        var NG= new Data("12345678901234567890123456789012345678901");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @Contact
        private String value;
    }
}
