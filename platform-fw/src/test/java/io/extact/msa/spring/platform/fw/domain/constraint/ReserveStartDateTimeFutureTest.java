package io.extact.msa.spring.platform.fw.domain.constraint;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import io.extact.msa.spring.test.assertj.ConstraintViolationSetAssert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest(classes = ValidationTestConfig.class, webEnvironment = WebEnvironment.NONE)
class ReserveStartDateTimeFutureTest {

    @Test
    void testValidate(@Autowired Validator validator) {

        Data OK= new Data(LocalDateTime.now().plusHours(1));
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // 利用開始日エラー(過去日)
        Data NG= new Data(LocalDateTime.now().minusDays(1));
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Future.message");
    }

    @lombok.Data
    static class Data {
        @ReserveStartDateTimeFuture
        private final LocalDateTime value;
    }
}
