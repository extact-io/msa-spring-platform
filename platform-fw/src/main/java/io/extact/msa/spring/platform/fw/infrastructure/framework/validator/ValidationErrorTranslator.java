package io.extact.msa.spring.platform.fw.infrastructure.framework.validator;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.extact.msa.spring.platform.fw.exception.response.SimpleErrorMessage;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorItem;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationErrorTranslator {

    private static final String REQUEST_PARAMETER_NONE_MESSAGE = "ex.RequestParameterNoneException.message";
    private static final String CONVERT_ERROR_MESSAGE = "ex.TypeMismatchException.massage";
    private static final String PARAMETER_ERROR_MESSAGE = "ex.ParameterErrorException.message";

    private final MessageSource messageSource;

    public ValidationErrorMessage from(TypeMismatchException e) {
        return from(e, defaultLocale());
    }

    // @Validatedに対する入力チェックエラー
    public ValidationErrorMessage from(BindingResult result, String errorReason, Locale locale) {

        Stream<ValidationErrorItem> fieldErrors = result.getFieldErrors().stream().map(error -> {
            String fieldName = resovleFieldName(error, locale);
            String message = messageSource.getMessage(error, locale);
            message = messageSource.getMessage(error, locale);
            return new ValidationErrorItem(fieldName, message);
        });

        Stream<ValidationErrorItem> globalErrors = result.getGlobalErrors().stream().map(error -> {
            String globalName = resovleObjectName(error, locale);
            String message = messageSource.getMessage(error, locale);
            return new ValidationErrorItem(globalName, message);
        });

        ValidationErrorMessage validationMessage = new ValidationErrorMessage(
                new SimpleErrorMessage(
                        errorReason,
                        parameterErrorMessage(locale)),
                Stream.concat(fieldErrors, globalErrors).toList());

        return validationMessage;
    }

    public ValidationErrorMessage from(MethodArgumentNotValidException e, Locale locale) {
        return from(e.getBindingResult(), e.getClass().getSimpleName(), locale);
    }

    public ValidationErrorMessage from(BindingResult bindingResult, String errorReason) {
        return from(bindingResult, errorReason, defaultLocale());
    }

    // @Validated以外(@NotNullなど)の入力チェックエラー
    public ValidationErrorMessage from(HandlerMethodValidationException e, Locale locale) {

        // MethodValidationResult
        //   -> ParameterValidationResult x n
        //      -> MessageSourceResolvable x n を1次元にflat化する
        List<MessageSourceResolvable> errors = e.getAllValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .toList();

        List<ValidationErrorItem> parameterErrors = errors.stream().map(error -> {
            String fieldName = resovleFieldName(error, locale);
            String message = messageSource.getMessage(error, locale);
            return new ValidationErrorItem(fieldName, message);
        }).toList();

        ValidationErrorMessage validationMessage = new ValidationErrorMessage(
                new SimpleErrorMessage(
                        e.getClass().getSimpleName(),
                        parameterErrorMessage(locale)),
                parameterErrors);

        return validationMessage;
    }

    // @ReqestParameterに対するパラメータなしエラー
    public ValidationErrorMessage from(MissingServletRequestParameterException e, Locale locale) {

        String fieldName = e.getParameterName();
        String message = messageSource.getMessage(
                REQUEST_PARAMETER_NONE_MESSAGE,
                null,
                defaultLocale());

        ValidationErrorItem errorItem = new ValidationErrorItem(fieldName, message);

        return new ValidationErrorMessage(
                new SimpleErrorMessage(
                        e.getClass().getSimpleName(),
                        parameterErrorMessage(locale)),
                List.of(errorItem));
    }

    // 入力のコンバートエラー
    public ValidationErrorMessage from(TypeMismatchException e, Locale locale) {

        String fieldName = e.getPropertyName();
        String requiredType = e.getRequiredType().getSimpleName();

        String message = messageSource.getMessage(
                CONVERT_ERROR_MESSAGE,
                new Object[] { requiredType },
                defaultLocale());

        ValidationErrorItem errorItem = new ValidationErrorItem(fieldName, message);

        return new ValidationErrorMessage(
                new SimpleErrorMessage(
                        e.getClass().getSimpleName(),
                        parameterErrorMessage(locale)),
                List.of(errorItem));
    }

    // -------------------------------------------------------- private methods

    private String parameterErrorMessage(Locale locale) {
        return messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null, locale);
    }

    private String resovleFieldName(MessageSourceResolvable errorMessage, Locale locale) {

        // MessageSourceResolvable#getArgumentsの0番目はエラーとなったフィールド固定
        // https://terasolunaorg.github.io/guideline/current/ja/ArchitectureInDetail/WebApplicationDetail/Validation.html#application-messages-properties
        return switch (errorMessage.getArguments()[0]) {
            case MessageSourceResolvable fieldMessage -> messageSource.getMessage(
                    new SelectableDefaultMessageResolver(fieldMessage),
                    locale);
            default -> "unknown field...";
        };
    }

    private String resovleObjectName(MessageSourceResolvable errorMessage, Locale locale) {

        // 相関チェックなどのオブジェクトレベルのエラーのフィールドのデフォルトメッセージは
        // 空になることがあるためこれを補完する
        return switch (errorMessage.getArguments()[0]) {

            case MessageSourceResolvable fieldMessage -> {

                if (StringUtils.hasText(fieldMessage.getDefaultMessage())) {
                    yield messageSource.getMessage(fieldMessage, locale);
                }

                DefaultMessageSourceResolvable objectName = new DefaultMessageSourceResolvable(
                        fieldMessage.getCodes(),
                        fieldMessage.getArguments(),
                        getLastElement(fieldMessage.getCodes())); // defaultMessage
                yield messageSource.getMessage(objectName, locale);
            }

            default -> "unknown field...";
        };
    }

    private Locale defaultLocale() {
        return Locale.getDefault();
    }

    private static <T> T getLastElement(T[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return null;
        }
        return array[array.length - 1];
    }

    @RequiredArgsConstructor
    static class SelectableDefaultMessageResolver implements MessageSourceResolvable {

        enum CodeType {
            LONG,
            SHORT
        }

        private final MessageSourceResolvable original;
        private final CodeType type;

        @Override
        public String[] getCodes() {
            return original.getCodes();
        }

        @Override
        public Object[] getArguments() {
            return original.getArguments();
        }

        @Override
        public String getDefaultMessage() {
            String[] codes = getCodes();
            if (codes == null) {
                return original.getDefaultMessage();
            }
            // TODO ここから。
            switch (type) {
                case LONG -> { codes[0] != null ? codes[0] : original.getDefaultMessage()}
            }
            return codes != null && codes[0] != null
                    ? codes[0] // フィールド名のpathが一番長いものをデフォルトにする
                    : original.getDefaultMessage();
        }
    }
}
