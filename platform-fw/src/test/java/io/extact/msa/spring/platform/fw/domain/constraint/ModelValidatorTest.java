package io.extact.msa.spring.platform.fw.domain.constraint;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.SmartValidator;

import io.extact.msa.spring.platform.fw.domain.constraint.EqualPairFields.EqualPairFieldsValidatable;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.model.ReferenceModel;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.SpringModelValidatorAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ModelValidatorTest {

    @Autowired
    private ModelValidator<TestModel> validator;

    @Configuration(proxyBeanMethods = false)
    @Import(ValidationConfiguration.class)
    static class TestConfig {

        @Bean
        ModelValidator<TestModel> domainValidator(SmartValidator validator) {
            return new SpringModelValidatorAdapter<TestModel>(validator);
        }
    }

    @Test
    void testObjectValidate() {

        TestModel test = new TestModel();

        test.id = new TestId(-1);
        test.pair1 = "1234";
        test.pair2 = "abcd";
        test.no = 99;
        validator.validateModel(test);

        test.nest = new NestModel(null, null);
        validator.validateModel(test);

        validator.validateModel(test, Add.class);
    }

    @Test
    void testFieldValidate() {

        TestModel test = new TestModel();

        test.id = new TestId(-1);
        test.pair1 = "1234";
        test.pair2 = "abcd";
        test.no = 99;

        validator.validateField(test, "id.id");
        validator.validateField(test, "pair1");
        validator.validateField(test, "pair2");
        validator.validateField(test, "no");
        validator.validateField(test, "pairFields");
        validator.validateField(test, "nest");

        test.nest = new NestModel(null, null);
        validator.validateField(test, "nest.val1");
        validator.validateField(test, "nest.val2");
    }

    static record PairFields(
            String val1,
            String val2) implements EqualPairFieldsValidatable {

        @Override
        public String getPair1() {
            return this.val1;
        }

        @Override
        public String getPair2() {
            return this.val2;
        }
    }

    static record TestId(
            @RmsId int id) implements Identity {
    }

    @EqualPairFields
    @AllArgsConstructor
    static class TestModel implements EqualPairFieldsValidatable, TestModelReference, DomainModel {

        private TestModel() {
        }

        @Getter
        private @NotNull @Valid TestId id;
        @Getter
        private @Size(max = 3) String pair1;
        @Getter
        private @Size(max = 3) String pair2;
        @Getter
        private @Max(value = 10, groups = { Default.class, Add.class }) int no;
        @Getter
        private @NotNull @Valid NestModel nest;

        private ModelValidator<TestModel> validator;

        @Override
        @EqualPairFields
        public PairFields getPairFields() {
            return new PairFields(pair1, pair2);
        }
    }

    interface TestModelReference extends ReferenceModel {
        TestId getId();
        String getPair1();
        String getPair2();
        int getNo();
        PairFields getPairFields();
        NestModel getNest();
    }

    @AllArgsConstructor
    static class NestModel implements NestModelReference {
        @Getter
        private @NotNull String val1;
        @Getter
        private @NotNull String val2;
    }

    interface NestModelReference {
        String getVal1();
        String getVal2();
    }
}
