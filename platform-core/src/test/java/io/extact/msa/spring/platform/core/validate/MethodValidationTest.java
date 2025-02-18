package io.extact.msa.spring.platform.core.validate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

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

        TestEntity entity = new TestEntity(0, 0, 0);

        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, () -> {
            testBean.noneGroupValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(2);

        thrown = assertThrows(ConstraintViolationException.class, () -> {
            testBean.defaultGroupValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(2);

        thrown = assertThrows(ConstraintViolationException.class, () -> {
            testBean.addGroupValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(1);

        thrown = assertThrows(ConstraintViolationException.class, () -> {
            testBean.updateGroupValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(1);

        assertThatCode(() -> {
            testBean.deleteGroupValidate(entity);
        }).doesNotThrowAnyException();

        thrown = assertThrows(ConstraintViolationException.class, () -> {
            testBean.addAndDefaultGroupValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(3);
    }

    @Test
    void testAnnotateVariationGroupDefValidation(@Autowired AnnotateVariationGroupDefTestBean groupAnnoteTestBean) {

        TestEntity entity = new TestEntity(0, 0, 0);

        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, () -> {
            groupAnnoteTestBean.applyTypeDefValidate(entity);
        });
        assertThat(thrown.getConstraintViolations()).hasSize(1);

        assertThatCode(() -> {
            groupAnnoteTestBean.defineTypeDiffGroupValidate(entity);
        }).doesNotThrowAnyException();
    }


    // ----------------------------------------------------- inner classes for test

    static record TestEntity(
            @Min(value = 100) //
            int value1,
            @Min(value = 100, groups = Add.class) //
            int value2,
            @Min(value = 100, groups = {
                    Default.class, Update.class }) //
            int value3){
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
