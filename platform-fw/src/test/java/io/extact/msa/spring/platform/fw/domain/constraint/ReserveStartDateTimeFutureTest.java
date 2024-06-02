package io.extact.msa.spring.platform.fw.domain.constraint;

import java.time.LocalDateTime;
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
class ReserveStartDateTimeFutureTest {

    @Test
    void testValidate(@Autowired Validator validator) {
        var OK= new Data(LocalDateTime.now().plusHours(1));
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 利用開始日エラー(過去日)
        var NG= new Data(LocalDateTime.now().minusDays(1));
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Future.message");
    }

    @AllArgsConstructor
    @Getter @Setter
    static class Data {
        @ReserveStartDateTimeFuture
        private LocalDateTime value;
    }
}
