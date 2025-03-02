package io.extact.msa.spring.platform.fw.infrastructure.framework;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import io.extact.msa.spring.platform.fw.domain.model.AbstractEntityModel;
import io.extact.msa.spring.platform.fw.domain.model.EntityModelView;
import io.extact.msa.spring.platform.fw.domain.model.Identity;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.domain.model.ValueModel;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields.EqualPairFieldsValidatable;
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
                .verifyItemOf("TestModel.id", NOT_NULL_MESSAGE);

        // given
        model.id = new TestId(-1);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.id.id", MIN_SIZE_1_MESSAGE);
    }

    @Test
    void testValidateFieldForRecordField() {

        // given
        TestModel model = new TestModel();
        model.id = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.id", NOT_NULL_MESSAGE);

        // given
        model.id = new TestId(-1);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.id.id", MIN_SIZE_1_MESSAGE);
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
                .verifyItemOf("TestModel.no", MAX_SIZE_10_MESSAGE);
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
                .verifyItemOf("TestModel.no", MAX_SIZE_10_MESSAGE);
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
                .verifyItemOf("TestModel.pairFields", NOT_NULL_MESSAGE);

        // given
        model.pairFields = new PairFields("123", "abc");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateModel(model);
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.pairFields", NOT_EQUALS_FIELD_PAIR);

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
                        "TestModel.pairFields", NOT_EQUALS_FIELD_PAIR,
                        "TestModel.pairFields.pair1", NOT_NULL_MESSAGE));

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
                        "TestModel.pairFields.pair1", NOT_NULL_MESSAGE,
                        "TestModel.pairFields.pair2", NOT_NULL_MESSAGE));
    }


    @Test
    void testValidateFieldForValueObjectField() {

        // given
        TestModel model = new TestModel();
        model.pairFields = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.pairFields", NOT_NULL_MESSAGE);

        // given
        model.pairFields = new PairFields("123", "abc");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.pairFields", NOT_EQUALS_FIELD_PAIR);

        // given
        model.pairFields = new PairFields(null, "123");
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "TestModel.pairFields", NOT_EQUALS_FIELD_PAIR,
                        "TestModel.pairFields.pair1", NOT_NULL_MESSAGE));

        // given
        model.pairFields = new PairFields(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "pairFields");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "TestModel.pairFields.pair1", NOT_NULL_MESSAGE,
                        "TestModel.pairFields.pair2", NOT_NULL_MESSAGE));
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
                .verifyItemOf("TestModel.nest", NOT_NULL_MESSAGE);

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
                        "TestModel.nest.val1", NOT_NULL_MESSAGE,
                        "TestModel.nest.val2", NOT_NULL_MESSAGE));
    }

    @Test
    void testValidateFieldForNestObjectField() {
        // given
        TestModel model = new TestModel();
        model.nest = null;
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModel.nest", NOT_NULL_MESSAGE);

        // given
        model.nest = new NestModel(null, null);
        // when
        e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest");
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf(Map.of(
                        "TestModel.nest.val1", NOT_NULL_MESSAGE,
                        "TestModel.nest.val2", NOT_NULL_MESSAGE));
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
                .verifyItemOf("TestModelForGroup.id", NOT_NULL_MESSAGE);
    }

    @Test
    void testValidateFieldForGroupById() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        // when
        assertThatCode(() -> {
            validator.validateField(model, "id"); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateField(model, "id", Add.class); // for Add Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "id", Update.class); // for Update Group
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModelForGroup.id", NOT_NULL_MESSAGE);
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
                .verifyItemOf("TestModelForGroup.no", MAX_SIZE_10_MESSAGE);
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
                .verifyItemOf("TestModelForGroup.no", MAX_SIZE_10_MESSAGE);
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
                .verifyItemOf("TestModelForGroup.nest.val2", NOT_NULL_MESSAGE);
    }


    @Test
    void testValidateFieldForGroupByNestObjectField() {

        // given
        TestModelForGroup model = new TestModelForGroup();
        model.nest = new NestModelForGroup("val1", null);
        // when
        assertThatCode(() -> {
            validator.validateField(model, "nest"); // for Default Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        // when
        assertThatCode(() -> {
            validator.validateField(model, "nest", Add.class); // for Add Group
        })
        // then
        .doesNotThrowAnyException();

        // given
        model.pairFields = null; // updateでエラーにならいことの確認
        // when
        RmsValidationException e = assertThrows(RmsValidationException.class, () -> {
            validator.validateField(model, "nest", Update.class); // for Update Group
        });
        // then
        RmsValidationExceptionAsserter.asserterTo(e)
                .verifyMessageHeader()
                .verifyItemOf("TestModelForGroup.nest.val2", NOT_NULL_MESSAGE);
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

    // ---------------------------------------------------------------------------- Model clssses for Test

    @AllArgsConstructor
    static class TestModel extends AbstractEntityModel implements TestModelView {

        TestModel() {
        }

        @Getter
        @NotNull
        @Valid
        private TestId id;

        @Getter
        @Max(10)
        private int no;

        @Getter
        @NotNull
        @Valid
        private PairFields pairFields;

        @Getter
        @NotNull
        @Valid
        private NestModel nest;
    }

    static record TestId(
            @RmsId int id) implements Identity {
    }

    @Value
    @EqualPairFields
    static class PairFields implements EqualPairFieldsValidatable, ValueModel {
        @Getter
        @NotNull
        private final String pair1;
        @Getter
        @NotNull
        private final String pair2;
    }

    interface TestModelView extends EntityModelView {
        TestId getId();

        int getNo();

        PairFields getPairFields();

        NestModel getNest();
    }

    @AllArgsConstructor
    static class NestModel implements ValueModel {
        @Getter
        @NotNull
        private String val1;
        @Getter
        @NotNull
        private String val2;
    }

    // ---------------------------------------------------------------------------- Model for Group clssses Test

    @AllArgsConstructor
    static class TestModelForGroup extends AbstractEntityModel {

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
        //@EqualPairFields
        @Valid
        private PairFields pairFields;

        @Getter
        @NotNull
        @Valid
        private NestModelForGroup nest;
    }


    @AllArgsConstructor
    static class NestModelForGroup implements ValueModel {
        @Getter
        @NotNull
        private String val1;
        @Getter
        @NotNull(groups = Update.class)
        private String val2;
    }
}
