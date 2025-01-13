package io.extact.msa.spring.platform.fw.infrastructure.framework;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.fw.domain.constraint.RmsId;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Add;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationGroups.Update;
import io.extact.msa.spring.platform.fw.domain.model.DomainModel;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.model.ReferenceModel;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorItem;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.SpringModelValidatorAdapter;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields.EqualPairFieldsValidatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SpringModelValidatorAdapterTest {

    @Autowired
    private ModelValidator validator;

    @Configuration(proxyBeanMethods = false)
    @Import(ValidatorConfig.class)
    static class TestConfig {
    }

    private static String VALIDATION_ERROR_MESSAGE = "バリデーションエラーが発生しました";
    private static String NOT_NULL_MESSAGE = "null は許可されていません";
    private static String MIN_SIZE_1_MESSAGE = "1 以上の値にしてください";
    private static String MAX_SIZE_10_MESSAGE = "10 以下の値にしてください";
    private static String NOT_EQUALS_FIELD_PAIR = "テスト値1とテスト値2を同じ値にしてください";

    @Test
    void testValidateModelForRecordField() {

        // given
        TestModel model = new TestModel(
                null, // ← エラーフィールド
                1,
                new PairFields("123", "123"),
                new NestModel("val1", "val2"));
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id", NOT_NULL_MESSAGE);

        // given
        model.id = new TestId(-1);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id.id", MIN_SIZE_1_MESSAGE);
    }

    @Test
    void testValidateFieldForRecordField() {

        // given
        TestModel model = new TestModel();
        model.id = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id"); // ← ここで例外が発生
            validator.validateField(model, "id.id");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id", NOT_NULL_MESSAGE);

        // given
        model.id = new TestId(-1);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id");
            validator.validateField(model, "id.id"); // ← ここで例外が発生
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id.id", MIN_SIZE_1_MESSAGE);
    }

    @Test
    void testValidateModelForSingleField() {
        // given
        TestModel model = new TestModel(
                new TestId(1),
                11, // ← エラーフィールド
                new PairFields("123", "123"),
                new NestModel("val1", "val2"));
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("no", MAX_SIZE_10_MESSAGE);
    }

    @Test
    void testValidateFieldSingleField() {
        // given
        TestModel model = new TestModel();
        model.no = 11;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "no");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("no", MAX_SIZE_10_MESSAGE);
    }

    @Test
    void testValidateModelForValueObjectField() {
        // given
        TestModel model = new TestModel(
                new TestId(1),
                1,
                null, // ← エラーフィールド
                new NestModel("val1", "val2"));
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields", NOT_NULL_MESSAGE);

        // given
        model.pairFields = new PairFields("123", "abc");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields", NOT_EQUALS_FIELD_PAIR);

        // given
        model.pairFields = new PairFields(null, "123");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "pairFields", NOT_EQUALS_FIELD_PAIR,
                        "pairFields.pair1", NOT_NULL_MESSAGE));

        // given
        model.pairFields = new PairFields(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "pairFields.pair1", NOT_NULL_MESSAGE,
                        "pairFields.pair2", NOT_NULL_MESSAGE));
    }


    @Test
    void testValidateFieldForValueObjectField() {

        // given
        TestModel model = new TestModel();
        model.pairFields = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields"); // ← ここで例外が発生
            validator.validateField(model, "pairFields.pair1");
            validator.validateField(model, "pairFields.pair2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields", NOT_NULL_MESSAGE);

        // given
        model.pairFields = new PairFields("123", "abc");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields"); // ← ここで例外が発生
            validator.validateField(model, "pairFields.pair1");
            validator.validateField(model, "pairFields.pair2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields", NOT_EQUALS_FIELD_PAIR);

        // given
        model.pairFields = new PairFields(null, "123");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields"); // ← ここで例外が発生
            validator.validateField(model, "pairFields.pair1");
            validator.validateField(model, "pairFields.pair2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields", NOT_EQUALS_FIELD_PAIR);

        // given
        model.pairFields = new PairFields(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields");
            validator.validateField(model, "pairFields.pair1"); // ← ここで例外が発生
            validator.validateField(model, "pairFields.pair2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("pairFields.pair1", NOT_NULL_MESSAGE);
    }

    @Test
    void testValidateModelForNestObjectField() {
        // given
        TestModel model = new TestModel(
                new TestId(1),
                1,
                new PairFields("123", "123"),
                null // ← エラーフィールド
        );
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("nest", NOT_NULL_MESSAGE);

        // given
        model.nest = new NestModel(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "nest.val1", NOT_NULL_MESSAGE,
                        "nest.val2", NOT_NULL_MESSAGE));
    }

    @Test
    void testValidateFieldForNestObjectField() {
        // given
        TestModel model = new TestModel();
        model.nest = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest");
            validator.validateField(model, "nest.val1");
            validator.validateField(model, "nest.val2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("nest", NOT_NULL_MESSAGE);

        // given
        model.nest = new NestModel(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest");
            validator.validateField(model, "nest.val1"); // ← ここで例外が発生
            validator.validateField(model, "nest.val2");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("nest.val1", NOT_NULL_MESSAGE);
    }


    // ---------------------------------------------------------------------------- for group test

    // Group指定をしても正常オブジェクトに対してはエラーが発生しないことの確認
    @Test
    void testValidateModelForGroupNormal() {

        // given
        TestModelForGroup model = new TestModelForGroup(
                new TestId(1),
                1,
                new PairFields("123", "123"),
                new NestModelForGroup("val1", "val2"));
        // when
        assertThatCode(() -> {
            validator.validateModel(model);
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateModel(model, Add.class);
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateModel(model, Update.class);
        })
        // then
        .doesNotThrowAnyException();
    }

    // 該当のバリデーションGroupが指定されない限りエラーならないこと
    // 他のテストケースのテスト観点も同じ
    @Test
    void testValidateModelForGroupById() {

        // given
        TestModelForGroup model = new TestModelForGroup(
                null, // エラーフィールド
                1,
                new PairFields("123", "123"),
                new NestModelForGroup("val1", "val2"));
        // when
        assertThatCode(() -> {
                validator.validateModel(model); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateModel(model, Add.class); // for Add Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.pairFields = null; // updateでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model, Update.class);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id", NOT_NULL_MESSAGE);
    }

    @Test
    void testValidateFieldForGroupById() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        // when
        assertThatCode(() -> {
                validator.validateField(model, "id");           // for Default Group
                // 値がある場合のみ満たすべき条件があるような場合は
                // nullのネストプロパティが辿られないようにする
                if (model.getId() != null) {
                    validator.validateField(model, "id.id");    // for Default Group
                }
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateField(model, "id", Add.class);        // for Add Group
            if (model.getId() != null) {
                validator.validateField(model, "id.id", Add.class); // for Add Group
            }
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id", Update.class);         // for Update Group
            if (model.getId() != null) {
                validator.validateField(model, "id.id", Update.class);  // for Update Group
            }
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("id", NOT_NULL_MESSAGE);
    }

    @Test
    void testValidateModelForGroupBySingleField() {

        // given
        TestModelForGroup model = new TestModelForGroup(
                new TestId(1),
                99,
                new PairFields("123", "123"),
                new NestModelForGroup("val1", "val2"));
        // when
        assertThatCode(() -> {
                validator.validateModel(model); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateModel(model, Update.class); // for Update Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.id = null; // addでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model, Add.class);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("no", MAX_SIZE_10_MESSAGE);
    }

    @Test
    void testValidateFieldForGroupBySingleField() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        model.no = 99;
        // when
        assertThatCode(() -> {
                validator.validateField(model, "no"); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateField(model, "no", Update.class); // for Update Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.id = null; // addでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "no", Add.class);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("no", MAX_SIZE_10_MESSAGE);
    }

    @Test
    void testValidateModelForGroupByNestObjectField() {

        // given
        TestModelForGroup model = new TestModelForGroup(
                new TestId(1),
                1,
                new PairFields("123", "123"),
                new NestModelForGroup("val1", null));
        // when
        assertThatCode(() -> {
                validator.validateModel(model); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateModel(model, Add.class); // for Add Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.pairFields = null; // updateでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model, Update.class);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("nest.val2", NOT_NULL_MESSAGE);
    }


    @Test
    void testValidateFieldForGroupByNestObjectField() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        model.nest = new NestModelForGroup("val1", null);
        // when
        assertThatCode(() -> {
                validator.validateField(model, "nest");         // for Default Group
                validator.validateField(model, "nest.val1");    // for Default Group
                validator.validateField(model, "nest.val2");    // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateField(model, "nest", Add.class);      // for Add Group
            validator.validateField(model, "nest.val1", Add.class); // for Add Group
            validator.validateField(model, "nest.val2", Add.class); // for Add Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.pairFields = null; // updateでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest", Update.class);      // for Update Group
            validator.validateField(model, "nest.val1", Update.class); // for Update Group
            validator.validateField(model, "nest.val2", Update.class); // for Update Group
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("nest.val2", NOT_NULL_MESSAGE);
    }


    // ---------------------------------------------------------------------------- for edge-case test

    @Test
    void testValidateUnknownField() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        // when
        NotReadablePropertyException e = assertThrows(NotReadablePropertyException.class, () -> {
            validator.validateField(model, "unknownField", Update.class);
        });
        // then
        assertThat(e).hasMessageContaining("unknownField");
    }

    @RequiredArgsConstructor
    static class RmsValidationExceptionAsserter {

        private final RmsValidationException e;

        static RmsValidationExceptionAsserter asserterTo(RmsValidationException e) {
            return new RmsValidationExceptionAsserter(e);
        }

        RmsValidationExceptionAsserter verifyMessageHeader() {
            assertThat(e).hasMessageContaining(VALIDATION_ERROR_MESSAGE);

            ValidationErrorMessage message = e.getErrorMessage();
            assertThat(message.errorReason()).isEqualTo(SpringModelValidatorAdapter.class.getSimpleName());
            assertThat(message.errorMessage()).isEqualTo(VALIDATION_ERROR_MESSAGE);

            return this;
        }

        RmsValidationExceptionAsserter verifyItemOf(String fieldName, String errorMessage) {
            ValidationErrorMessage message = e.getErrorMessage();
            List<ValidationErrorItem> items = message.validationErrorItems();
            assertThat(items)
                    .containsExactlyInAnyOrderElementsOf(
                            List.of(new ValidationErrorItem(fieldName, errorMessage)));
            return this;
        }

        RmsValidationExceptionAsserter verifyItemOf(Map<String, String> expectedMap) {

            List<ValidationErrorItem> expectItems = expectedMap.entrySet().stream()
                    .map(entry -> new ValidationErrorItem(entry.getKey(), entry.getValue()))
                    .toList();

            ValidationErrorMessage message = e.getErrorMessage();
            List<ValidationErrorItem> items = message.validationErrorItems();

            assertThat(items).containsExactlyInAnyOrderElementsOf(expectItems);

            return this;
        }
    }


    // ---------------------------------------------------------------------------- Model clssses for Test

    @Value
    static class PairFields implements EqualPairFieldsValidatable {
        @Getter
        private final @NotNull String pair1;
        @Getter
        private final @NotNull String pair2;
    }

    static record TestId(
            @RmsId int id) implements Identity {
    }

    @AllArgsConstructor
    static class TestModel implements TestModelReference, DomainModel {

        TestModel() {
        }

        @Getter
        private @NotNull @Valid TestId id;
        @Getter
        private @Max(10) int no;
        @Getter
        private @NotNull @EqualPairFields @Valid PairFields pairFields;
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

    // ---------------------------------------------------------------------------- Model for Group clssses Test

    @AllArgsConstructor
    static class TestModelForGroup implements DomainModel {

        TestModelForGroup() {
        }

        @Getter
        @NotNull(groups = Update.class)
        @Valid
        private TestId id;

        @Getter
        @Max(value = 10, groups = Add.class)
        private int no;

        @Getter
        @NotNull
        @EqualPairFields
        @Valid
        private PairFields pairFields;

        @Getter
        @NotNull
        @Valid
        private NestModelForGroup nest;

        @Override
        public void configureValidator(ModelValidator validator) {
        }
    }


    @AllArgsConstructor
    static class NestModelForGroup implements NestModelReference {
        @Getter
        @NotNull
        private String val1;
        @Getter
        @NotNull(groups = Update.class)
        private String val2;
    }
}
