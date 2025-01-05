package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.constraint.EqualPairFields;
import io.extact.msa.spring.platform.fw.domain.constraint.EqualPairFields.EqualPairFieldsValidatable;
import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.model.ReferenceModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SpringModelValidatorAdapterTest {

    @Autowired
    private ModelValidator validator;

    @Configuration(proxyBeanMethods = false)
    @Import(ValidatorConfig.class)
    static class TestConfig {
    }

    @Test
    void testObjectValidate() {

        TestModel test = new TestModel();

        test.id = new TestId(-1);
        test.no = 99;
        validator.validateModel(test);

        test.pairFields = new PairFields("1234", "abcd");
        validator.validateModel(test);

        test.pairFields = new PairFields("123", "abc");
        validator.validateModel(test);

        test.nest = new NestModel(null, null);
        validator.validateModel(test);
    }

    @Test
    void testFieldValidate() {

        TestModel test = new TestModel();

        test.id = new TestId(-1);
        test.no = 99;

        validator.validateField(test, "id.id");
        validator.validateField(test, "no");
        validator.validateField(test, "pairFields");
        validator.validateField(test, "nest");

        test.pairFields = new PairFields("1234", "abcd");
        validator.validateField(test, "pairFields");

        test.nest = new NestModel(null, null);
        validator.validateField(test, "nest.val1");
        validator.validateField(test, "nest.val2");
    }


    // --- Model clssses for Test

    @Value
    static class PairFields implements EqualPairFieldsValidatable {
        @Getter
        private final @Size(max = 3) String pair1;
        @Getter
        private final @Size(max = 3) String pair2;
    }

    static record TestId(
            @RmsId int id) implements Identity {
    }

    @AllArgsConstructor
    static class TestModel implements TestModelReference, DomainModel {

        private TestModel() {
        }

        @Getter
        private @NotNull @Valid TestId id;
        @Getter
        private @NotNull @EqualPairFields @Valid PairFields pairFields;
        @Getter
        private @Max(value = 10) int no;
        @Getter
        private @NotNull @Valid NestModel nest;

        @Override
        public void configureValidator(ModelValidator validator) {
        }
    }

    interface TestModelReference extends ReferenceModel {
        TestId getId();
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
