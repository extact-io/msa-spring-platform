package io.extact.msa.spring.platform.fw.domain.constraint;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import io.extact.msa.spring.test.assertj.ConstraintViolationSetAssert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest(classes = ValidationTestConfig.class, webEnvironment = WebEnvironment.NONE)
class PhoneNumberTest {

    @Test
    void testValidate(@Autowired Validator validator) {

        Data OK= new Data("1234567890");
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 電話番号(使用可能文字以外)
        Data NG= new Data("12345%");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("PhoneNumberCharacter");

        // 電話番号(14文字より大きい)
        NG= new Data("123456789012345");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @lombok.Data
    static class Data {
        @PhoneNumber
        private final String value;
    }
}
