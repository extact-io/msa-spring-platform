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
class NoteTest {

    @Test
    void testValidate(@Autowired Validator validator) {

        // メモ(64文字以内)
        Data OK= new Data("１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４"); // 境界値:OK
        Set<ConstraintViolation<Data>> result = validator.validate(OK);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // メモ(64文字より以上)
        Data NG= new Data("12345678901234567890123456789012345678901234567890123456789012345"); // 境界値:NG
        result = validator.validate(NG);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("value")
            .hasMessageEndingWith("Size.message");
    }

    @lombok.Data
    static class Data {
        @Note
        private final String value;
    }
}
