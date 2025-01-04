package io.extact.msa.spring.platform.fw.domain.constraint;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import io.extact.msa.spring.platform.fw.domain.constraint.EqualPairFields.EqualPairFieldsValidatable;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.DomainValidator;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.SpringDomainValidator;
import lombok.AllArgsConstructor;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DomainValidatorTest {

    @Configuration(proxyBeanMethods = false)
    @Import(ValidationConfiguration.class)
    static class TestConfig {
        @Bean
        DomainValidator domainValidator(SmartValidator validator) {
            return new SpringDomainValidator(validator, TestModel.class);
        }
    }

    @Test
    void testModelValidate(@Autowired DomainValidator validator, @Autowired MessageSource messageSource) {

        TestModel testModel = new TestModel(new TestModelId(1), null, null, 0, validator);
        testModel.verify();

        //validator.validateProperty("pair1", "123456", Default.class);
        testModel.setPair2("123456");
    }


    @Test
    void testDefaultMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        PairFields NG = new PairFields("value1", "value2");

        Errors errors = validator.validateObject(NG);

        String defaultMessage = errors.getGlobalError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("{1}と{2}を同じ値にしてください");

        String resolvedMessage = messageSource.getMessage(errors.getGlobalError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("テスト値1とテスト値2を同じ値にしてください");
    }

    @Test
    void testCustomMessage(@Autowired Validator validator, @Autowired MessageSource messageSource) {

        Value val = new Value("12345");
        Errors errors = validator.validateObject(val);

        String defaultMessage = errors.getFieldError().getDefaultMessage();
        assertThat(defaultMessage).isEqualTo("override default message, parameter=1,3");

        // MessageSourceでメッセージを上書き
        String resolvedMessage = messageSource.getMessage(errors.getFieldError(), Locale.getDefault());
        assertThat(resolvedMessage).isEqualTo("名前は1から3のサイズにしてください");
    }

    @EqualPairFields
    static record PairFields(
            @Size(min = 2) String val1,
            @Size(min = 2) String val2) implements EqualPairFieldsValidatable {
    }

    static record Value(
            @Size(min = 1, max = 3) //
            String name) {
    }

    @Valid
    @AllArgsConstructor
    @EqualPairFields
    static class TestModel implements DomainModel, EqualPairFieldsValidatable {

        private TestModel() {
        }

        private @NotNull @Valid TestModelId id;
        private @Size(max = 5) String pair1;
        private @Size(max = 5) String pair2;
        private @Max(10) int no;

        private DomainValidator validator;

        @Override
        public void verify() {
            validator.validateModel(this, Default.class, Add.class);
        }

        @Override
        public void configureValidator(DomainValidator validator) {
            this.validator = validator;
        }

        @Override
        public Identity getId() {
            return this.id;
        }

        @Override
        public String val1() {
            return this.pair1;
        }

        @Override
        public String val2() {
            return this.pair2;
        }

        public void setPair2(String newVal) {
            // ホルダーとして新しいインスタンスを作成して、それでバリデートすればOKだ！！！
            // オレって頭がいい！
            TestModel test = new TestModel();
            test.pair2 = newVal;
            validator.validateProperty("pair2", test, Default.class);
        }
    }

    static record TestModelId(
            @RmsId
            int id) implements Identity {
    }
}
