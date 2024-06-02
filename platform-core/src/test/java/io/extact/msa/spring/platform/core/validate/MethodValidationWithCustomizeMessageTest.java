package io.extact.msa.spring.platform.core.validate;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MethodValidationWithCustomizeMessageTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        VariationTestBean testBean() {
            return new VariationTestBean();
        }

        @Bean
        MethodValidationPostProcessor validationPostProcessor() {
            MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
            processor.setAdaptConstraintViolations(true);
            return processor;
        }
    }

    // ----------------------------------------------------- test methods

    @Test
    void testGroupVariationValidation(@Autowired VariationTestBean testBean) {

        TestEntity entity = new TestEntity(null, 1);
        entity.setDetails(List.of(new Detail(), new Detail()));
        entity.setDetailMap(Map.of("No.1", new Detail()));

        MethodValidationException actual = catchThrowableOfType(
                () -> testBean.validate(null, List.of(new Detail(), new Detail()), null, entity, List.of(entity)),
                MethodValidationException.class);


        // 1チェック項目に複数エラーが発生する可能性があるためParameterValidationResultは
        // チェック項目 x 発生エラーの2次元配列の構造になってるので1次元のエラーメッセージにflatしている
        List<ErrorItem> errors = actual.getAllValidationResults().stream()
                .map(paramResult -> {

                    // メソッド引数自体がListやMapの場合はそのindexが項目名に入っていないので
                    // 最初に取得して後で項目名に付加できるようにする
                    Object containerPos = paramResult.getContainerIndex() != null
                            ? paramResult.getContainerIndex()
                            : paramResult.getContainerKey();

                    // エラーメッセージ情報への展開
                    List<ErrorItem> errorItemNames = switch (paramResult) {
                        // メソッドのパラメーター値に直接関係するエラーの場合
                        case ParameterErrors nestedErrors -> resolveErrorItemNames(nestedErrors.getResolvableErrors(),
                                containerPos, true);
                        // オブジェクトメソッドパラメーターのネストされたエラーの場合
                        case ParameterValidationResult directErrors -> resolveErrorItemNames(
                                directErrors.getResolvableErrors(),
                                containerPos, false);
                    };

                    return errorItemNames;
                })
                .flatMap(List::stream)
                .toList();

        System.out.println(errors);
    }

    List<ErrorItem> resolveErrorItemNames(List<MessageSourceResolvable> errors, Object containerPos, boolean useFirstCode) {
        return errors.stream().map(error -> {

            Object arg = error.getArguments()[0];
            if (!(arg instanceof MessageSourceResolvable)) {
                return new ErrorItem("-", error.getDefaultMessage());
            }

            MessageSourceResolvable errorItemArg = (MessageSourceResolvable) arg;

            String[] nameCodeCandidates = errorItemArg.getCodes();
            int nameCodePos = useFirstCode ? 0 : nameCodeCandidates.length - 1;
            String errorItemName = nameCodeCandidates[nameCodePos];

            if (containerPos != null) {
                errorItemName = normalizeItemName(errorItemName, containerPos);
            }

            return new ErrorItem(errorItemName, error.getDefaultMessage());

        }).toList();
    }

    // ----------------------------------------------------- inner classes for test

    private String normalizeItemName(String errorItemName, Object containerPos) {
        int dotIntdex = errorItemName.indexOf('.');
        if (dotIntdex == -1) {
            return errorItemName;
        }
        return errorItemName.substring(0, dotIntdex) + "[" + containerPos + "]" + errorItemName.substring(dotIntdex);
    }


    static record ErrorItem(String itemName, String message) {
    }


    @Data
    public static class TestEntity {
        @NotNull
        private final Integer value1;
        @Min(100)
        private final Integer value2;
        @Valid
        @NotNull
        private List<Detail> details;
        @Valid
        @NotNull
        private Map<String, Detail> detailMap;
    }

    @Data
    public static class Detail {
        @NotNull
        private String message;
    }

    @Validated // Interceptorを掛けるためにクラスへのアノテートは必要
    public static class VariationTestBean {

        public int validate(@NotNull Integer param, @NotNull @Valid List<Detail> details,
                @NotNull @Valid Map<String, Detail> detailMap,
                @Valid TestEntity entity,
                @Valid List<TestEntity> entitys
                ) {
            return 101;
        }
    }
}
