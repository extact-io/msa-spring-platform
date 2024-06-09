package io.extact.msa.spring.platform.fw.domain.constraint;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import io.extact.msa.spring.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.msa.spring.test.assertj.ConstraintViolationSetAssert;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest(classes = ValidationTestConfig.class, webEnvironment = WebEnvironment.NONE)
class BeforeAfterDateTimeTest {

    @Test
    void testValidate(@Autowired Validator validator) {

        Data OK = new Data(LocalDateTime.now().minusHours(1), LocalDateTime.now());
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
                .hasNoViolations();

        Data NG = new Data(LocalDateTime.now(), LocalDateTime.now().minusHours(1));
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
                .hasSize(1)
                .hasMessageEndingWith("BeforeAfterDateTime");
    }

    @lombok.Data
    @BeforeAfterDateTime
    static class Data implements BeforeAfterDateTimeValidatable {
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
    }
}
