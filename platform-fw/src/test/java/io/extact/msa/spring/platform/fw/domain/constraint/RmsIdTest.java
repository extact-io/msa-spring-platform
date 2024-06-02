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
class RmsIdTest {

    @Test
    void testValidate(@Autowired Validator validator) {
        var OK= new Data(1);
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // IDエラー(null)
        var NG= new Data(null);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("NotNull.message");

        // IDエラー(1未満)
        NG= new Data(0);
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Min.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @RmsId
        private Integer value;
    }
}
