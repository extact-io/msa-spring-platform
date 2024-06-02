package io.extact.msa.spring.platform.core.validate;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.Data;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MethodValidationTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        GroupVariationTestBean testBean() {
            return new GroupVariationTestBean();
        }

        @Bean
        AnnotateVariationGroupDefTestBean groupAnnoteTestBean() {
            return new AnnotateVariationGroupDefTestBean();
        }
    }

    // ValidationGroups for Test
    public interface Add {}
    public interface Update {}
    public interface Delete {}

    // ----------------------------------------------------- test methods

    @Test
    void testGroupVariationValidation(@Autowired GroupVariationTestBean testBean) {

        TestEntity entity = new TestEntity();
        entity.setValue1(0);
        entity.setValue2(0);
        entity.setValue3(0);

        ConstraintViolationException actual = catchThrowableOfType(() ->
            testBean.noneGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.defaultGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.addGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(1);

        actual = catchThrowableOfType(() ->
            testBean.updateGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(1);

        actual = catchThrowableOfType(() ->
            testBean.deleteGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual).isNull();;

        actual = catchThrowableOfType(() ->
            testBean.addAndDefaultGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(3);
    }

    @Test
    void testAnnotateVariationGroupDefValidation(@Autowired AnnotateVariationGroupDefTestBean groupAnnoteTestBean) {

        TestEntity entity = new TestEntity();
        entity.setValue1(0);
        entity.setValue2(0);
        entity.setValue3(0);

        ConstraintViolationException actual = catchThrowableOfType(() ->
            groupAnnoteTestBean.applyTypeDefValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(1);

        actual = catchThrowableOfType(() ->
            groupAnnoteTestBean.defineTypeDiffGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual).isNull();
    }


    // ----------------------------------------------------- inner classes for test

    @Data
    public static class TestEntity {

        @Min(value = 100)
        private int value1;
        @Min(value = 100, groups = Add.class)
        private int value2;
        @Min(value = 100, groups = { Default.class, Update.class })
        private int value3;

        public int getValue1() {
            return value1;
        }
        public void setValue1(int value1) {
            this.value1 = value1;
        }
        public int getValue2() {
            return value2;
        }
        public void setValue2(int value2) {
            this.value2 = value2;
        }
        public int getValue3() {
            return value3;
        }
        public void setValue3(int value3) {
            this.value3 = value3;
        }
    }

    @Validated // Interceptorを掛けるためにクラスへのアノテートは必要
    public static class GroupVariationTestBean {

        public void noneGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @Validated(Default.class)
        public void defaultGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @Validated(Add.class)
        public void addGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @Validated(Update.class)
        public void updateGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @Validated(Delete.class)
        public void deleteGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @Validated({ Default.class, Add.class })
        public void addAndDefaultGroupValidate(@Valid TestEntity entity) {
            // nop
        }
    }

    @Validated(Add.class)
    public static class AnnotateVariationGroupDefTestBean {

        // メソッドにGroup指定なし
        public void applyTypeDefValidate(@Valid TestEntity entity) {
            // nop
        }

        // メソッドにクラスと異なるGroupを指定(指定を上書き)
        @Validated(Delete.class)
        public void defineTypeDiffGroupValidate(@Valid TestEntity entity) {
            // nop
        }
    }
}
