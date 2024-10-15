package io.extact.msa.spring.platform.fw.it;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.controller.ExceptionHandled;
import io.extact.msa.spring.platform.fw.controller.RestControllerConfig;
import io.extact.msa.spring.platform.fw.controller.RestControllerExceptionHandler;
import io.extact.msa.spring.platform.fw.domain.constraint.ValidationConfiguration;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorItem;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.it.ExceptionErrorHandlerTest.PairFieldsEquals.PairFieldsEqualsValidatable;
import io.extact.msa.spring.platform.fw.it.ExceptionErrorHandlerTest.PairFieldsEquals.PairFieldsEqualsValidator;
import io.extact.msa.spring.test.spring.EnableAutoConfigurationWithoutSecurity;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;
import lombok.Data;

/**
 * {@link RestControllerExceptionHandler}と{@link RestClientErrorHandler}の両方を使った
 * Controller -> RestClient間のエラーハンドリングの結合テスト
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.main.banner-mode=off")
class ExceptionErrorHandlerTest {

    private static final String PARAMETER_ERROR_MESSAGE = "ex.ParameterErrorException.message";
    private static final String CONVERT_ERROR_MESSAGE = "ex.TypeMismatchException.massage";

    @Autowired
    private ValidationTestClient client;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutSecurity
    @Import({ RestControllerConfig.class, ValidationConfiguration.class })
    static class TestConfig {

        @Bean
        ValidationTestController validationTestController() {
            return new ValidationTestController();
        }

        @Bean
        ValidationTestClient validationTestClient(Environment env) {
            RestClientErrorHandler errorHandler = new RestClientErrorHandler(new ErrorMessageDeserializer());

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .defaultStatusHandler(errorHandler)
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            return factory.createClient(ValidationTestClient.class);
        }
    }

    @Test
    void occurNotFoundBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurNotFoundBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur not found");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
                        });
    }

    @Test
    void occurDuplicateBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurDuplicateBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur duplicate");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.DUPLICATE);
                        });
    }

    @Test
    void occurForbiddenBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurForbiddenBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur forbidden");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.FORBIDDEN);
                        });
    }

    @Test
    void occurServiceUnavailableExceptionTest() {
        assertThatThrownBy(() -> client.occurServiceUnavailableException())
                .isInstanceOfSatisfying(
                        RmsServiceUnavailableException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("service unavailable");
                        });
    }

    @Test
    void pathParamSingleTest(@Autowired MessageSource messageSource) {
        // このパターンはキッチリテストを行う
        assertThatThrownBy(() -> client.pathParamSingle("1"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null, Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason()).isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(1);
                            assertThat(error.getErrorItems().get(0).getFieldName()).isEqualTo("val1");
                            assertThat(error.getErrorItems().get(0).getMessage()).contains("サイズにしてください");
                        });
    }

    @Test
    void pathParamMultiTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.pathParamMulti("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(2);
                        });
    }

    @Test
    void queryParamSingleTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.queryParamSingle("1"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(1);
                        });
    }

    @Test
    void queryParamMultiTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.queryParamMulti("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(2);
                        });
    }

    @Test
    void queryParamDtoTest(@Autowired MessageSource messageSource) {
        // このパターンはキッチリテストを行う
        assertThatThrownBy(() -> client.queryParamDto("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(3);

                            Map<String, String> itemMap = error.getErrorItems().stream()
                                    .collect(Collectors.toMap(
                                            ValidationErrorItem::getFieldName,
                                            ValidationErrorItem::getMessage));
                            assertThat(itemMap).containsKey("値1");
                            assertThat(itemMap.get("値1")).contains("サイズにしてください");
                            assertThat(itemMap).containsKey("値2");
                            assertThat(itemMap.get("値2")).contains("サイズにしてください");
                            assertThat(itemMap).containsKey("入力値");
                            assertThat(itemMap.get("入力値")).contains("同じ値にしてください");
                        });
    }

    @Test
    void bodyParamDtoTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.bodyParamDto(new ParamDto("1", "2")))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(3);
                        });
    }

    @Test
    void convertedTypePathParamTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.convertedTypePathParam("a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);


                            String fieldErrorMessage = messageSource.getMessage(
                                    CONVERT_ERROR_MESSAGE,
                                    new Object[] { int.class.getSimpleName() },
                                    Locale.getDefault());

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(errorMessage);
                            assertThat(error.getErrorItems()).hasSize(1);
                            assertThat(error.getErrorItems().get(0).getFieldName()).isEqualTo("val1");
                            assertThat(error.getErrorItems().get(0).getMessage()).contains(fieldErrorMessage);
                        });
    }

    @Test
    void convertedTypeQueryParamTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.convertedTypeQueryParam("a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(errorMessage);
                            assertThat(error.getErrorItems()).hasSize(1);
                        });
    }

    @Test
    void queryParamDtoWithRequestParam1Test(@Autowired MessageSource messageSource) {
        // MethodArgumentNotValidExceptionになる(ParamDto格納エラでコンバートエラーまで行かず)
        assertThatThrownBy(() -> client.queryParamDtoWithRequestParam("1", "2", "a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(message);
                            assertThat(error.getErrorItems()).hasSize(3);
                        });
    }

    @Test
    void queryParamDtoWithRequestParam2Test(@Autowired MessageSource messageSource) {
        // MethodArgumentTypeMismatchExceptionになる(ParamDto格納成功でコンバートエラー)
        assertThatThrownBy(() -> client.queryParamDtoWithRequestParam("123", "123", "a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.getErrorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.getErrorMessage()).isEqualTo(errorMessage);
                            assertThat(error.getErrorItems()).hasSize(1);
                        });
    }

    @Test
    void occurSystemExceptionTest() {
        assertThatThrownBy(() -> client.occurSystemException())
                .isInstanceOfSatisfying(
                        RmsSystemException.class,
                        e -> assertThat(e.getMessage()).contains("occur system error"));
    }

    @Test
    void occurUnknowExceptionTest() {
        assertThatThrownBy(() -> client.occurUnknowException())
                .isInstanceOfSatisfying(
                        RmsSystemException.class,
                        e -> assertThat(e.getMessage()).contains("json deserialize error"));
    }

    @HttpExchange
    static interface ValidationTestClient {

        @GetExchange("/pathParamSingle/{val1}")
        void pathParamSingle(@PathVariable String val1);

        @GetExchange("/pathParamMulti/{val1}/{val2}")
        void pathParamMulti(@PathVariable String val1, @PathVariable String val2);

        @GetExchange("/queryParamSingle")
        void queryParamSingle(@RequestParam String val1);

        @GetExchange("/queryParamMulti")
        void queryParamMulti(@RequestParam String val1, @RequestParam String val2);

        @GetExchange("/queryParamDto")
        void queryParamDto(@RequestParam String val1, @RequestParam String val2);

        @PostExchange("/bodyParamDto")
        void bodyParamDto(@Validated @RequestBody ParamDto dto);

        @GetExchange("/convertedTypePathParam/{val1}")
        void convertedTypePathParam(@PathVariable String val1);

        @GetExchange("/convertedTypeQueryParam")
        void convertedTypeQueryParam(@RequestParam String val1);

        @GetExchange("/queryParamDtoWithRequestParam")
        void queryParamDtoWithRequestParam(
                @RequestParam String val1,
                @RequestParam String val2,
                @RequestParam String val3);

        @GetExchange("/occurNotFoundBusinessFlowException")
        void occurNotFoundBusinessFlowException();

        @GetExchange("/occurDuplicateBusinessFlowException")
        void occurDuplicateBusinessFlowException();

        @GetExchange("/occurForbiddenBusinessFlowException")
        void occurForbiddenBusinessFlowException();

        @GetExchange("/occurServiceUnavailableException")
        void occurServiceUnavailableException();

        @GetExchange("/occurSystemException")
        void occurSystemException();

        @GetExchange("/occurUnknownException")
        void occurUnknowException();
    }

    @Data
    @PairFieldsEquals
    static class ParamDto implements PairFieldsEqualsValidatable {
        @Size(min = 2)
        private final String val1;
        @Size(min = 2)
        private final String val2;
    }

    @RestController
    @ExceptionHandled
    public static class ValidationTestController {

        @GetMapping("/pathParamSingle/{val1}")
        public void pathParamSingle(@PathVariable @Size(min = 2) String val1) {
            // NOP
        }

        @GetMapping("/pathParamMulti/{val1}/{val2}")
        public void pathParamMulti(@PathVariable @Size(min = 2) String val1, @PathVariable @Size(min = 2) String val2) {
            // NOP
        }

        @GetMapping("/queryParamSingle")
        public void queryParamSingle(@RequestParam @Size(min = 2) String val1) {
            // NOP
        }

        @GetMapping("/queryParamMulti")
        public void queryParamMulti(@RequestParam @Size(min = 2) String val1, @RequestParam @Size(min = 2) String val2) {
            // NOP
        }

        @GetMapping("/queryParamDto")
        public void queryParamDto(@Validated ParamDto dto) {
            // NOP
        }

        @PostMapping("/bodyParamDto")
        public void bodyParamDto(@Validated @RequestBody ParamDto dto) {
            // NOP
        }

        @GetMapping("/convertedTypePathParam/{val1}")
        public void convertedTypePathParam(@PathVariable int val1) {
            // NOP
        }

        @GetMapping("/convertedTypeQueryParam")
        public void convertedTypeQueryParam(@RequestParam int val1) {
            // NOP
        }

        @GetMapping("/queryParamDtoWithRequestParam")
        public void queryParamDtoWithRequestParam(@Validated ParamDto dto, @RequestParam int val3) {
            // NOP
        }

        @GetMapping("/occurNotFoundBusinessFlowException")
        public void occurNotFoundBusinessFlowException() {
            throw new BusinessFlowException("occur not found.", CauseType.NOT_FOUND);
        }

        @GetMapping("/occurDuplicateBusinessFlowException")
        public void occurDuplicateBusinessFlowException() {
            throw new BusinessFlowException("occur duplicate.", CauseType.DUPLICATE);
        }

        @GetMapping("/occurForbiddenBusinessFlowException")
        public void occurForbiddenBusinessFlowException() {
            throw new BusinessFlowException("occur forbidden.", CauseType.FORBIDDEN);
        }

        @GetMapping("/occurServiceUnavailableException")
        public void occurServiceUnavailableException() {
            throw new RmsServiceUnavailableException("service unavailable.");
        }

        @GetMapping("/occurSystemException")
        public void occurSystemException() {
            throw new RmsSystemException("occur system error.");
        }

        @GetMapping("/occurUnknownException")
        public ResponseEntity<Object> occurUnknownException() {
            Exception occuredException = new IllegalStateException("unknown exception.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("rms-exception", occuredException.getClass().getSimpleName())
                    .body(occuredException.getMessage());
        }
    }

    @Documented
    @Constraint(validatedBy = { PairFieldsEqualsValidator.class })
    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    public @interface PairFieldsEquals {

        String message() default "{bv.PairFieldsEquals.message}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        String val1() default "input1";

        String val2() default "input2";

        @Target({ TYPE, ANNOTATION_TYPE })
        @Retention(RUNTIME)
        @Documented
        public @interface List {
            PairFieldsEquals[] value();
        }

        public static class PairFieldsEqualsValidator implements ConstraintValidator<PairFieldsEquals, PairFieldsEqualsValidatable> {

            public boolean isValid(PairFieldsEqualsValidatable bean, ConstraintValidatorContext context) {
                if (bean.getVal1() == null || bean.getVal2() == null) {
                    return true; // チェックしない
                }
                return bean.getVal1().equals(bean.getVal2());
            }
        }

        public interface PairFieldsEqualsValidatable {
            String getVal1();
            String getVal2();
        }
    }

}
