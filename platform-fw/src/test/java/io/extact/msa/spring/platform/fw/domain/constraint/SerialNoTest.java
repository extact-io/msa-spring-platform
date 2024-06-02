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
class SerialNoTest {

    @Test
    void testValidate(@Autowired Validator validator) {
        var OK= new Data("123456789012345"); // 境界値:OK
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // シリアル番号エラー(null)
        var NG= new Data(null);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotBlank.message");

        // シリアル番号エラー(空文字列)
        NG= new Data("");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotBlank.message");

        // シリアル番号エラー(使用可能文字以外)
        NG= new Data("A_0001");
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("SerialNoCharacter.message");

        // シリアル番号エラー(15文字より大きい)
        NG= new Data("1234567890123456"); // 境界値:NG
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @SerialNo
        private String value;
    }
}
