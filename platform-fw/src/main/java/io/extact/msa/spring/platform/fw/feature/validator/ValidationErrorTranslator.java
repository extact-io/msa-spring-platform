package io.extact.msa.spring.platform.fw.feature.validator;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage.MessageItem;
import io.extact.msa.spring.platform.fw.feature.validator.ValidationErrorTranslator.SelectableDefaultMessageResolver.CodeType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationErrorTranslator {

    private static final String REQUEST_PARAMETER_NONE_MESSAGE = "ex.RequestParameterNoneException.message";
    private static final String CONVERT_ERROR_MESSAGE = "ex.TypeMismatchException.massage";
    private static final String PARAMETER_ERROR_MESSAGE = "ex.ParameterErrorException.message";

    private final MessageSource messageSource;

    // @Validatedに対する入力チェックエラー
    public ValidationErrorMessage from(BindingResult result, String errorReason, Locale locale) {

        Stream<MessageItem> fieldErrors = result.getFieldErrors().stream().map(error -> {
            String fieldName = resovleFieldName(error, CodeType.LONG, locale);
            String message = messageSource.getMessage(error, locale);
            message = messageSource.getMessage(error, locale);
            return new MessageItem(fieldName, message);
        });

        Stream<MessageItem> globalErrors = result.getGlobalErrors().stream().map(error -> {
            String globalName = resovleObjectName(error, locale);
            String message = messageSource.getMessage(error, locale);
            return new MessageItem(globalName, message);
        });

        ValidationErrorMessage errorMessage = new ValidationErrorMessage(
                errorReason,
                parameterErrorMessage(locale),
                Stream.concat(fieldErrors, globalErrors).toList());

        return errorMessage;
    }

    public ValidationErrorMessage from(MethodArgumentNotValidException thrown, Locale locale) {
        return from(thrown.getBindingResult(), thrown.getClass().getSimpleName(), locale);
    }

    public ValidationErrorMessage from(BindingResult bindingResult, String errorReason) {
        return from(bindingResult, errorReason, defaultLocale());
    }

    // @Validated以外(@NotNullなど)の入力チェックエラー
    public ValidationErrorMessage from(HandlerMethodValidationException thrown, Locale locale) {

        /*
         * MethodValidationResultの2次元配列イメージをflat化する
         *  - MethodValidationResult
         *  - -> ParameterValidationResult x n
         *  - -> MessageSourceResolvable x n を1次元にflat化する
         */
        List<MessageSourceResolvable> errors = thrown.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .toList();

        List<MessageItem> parameterErrors = errors.stream().map(error -> {
            String fieldName = resovleFieldName(error, CodeType.DEFAULT, locale);
            String message = messageSource.getMessage(error, locale);
            return new MessageItem(fieldName, message);
        }).toList();

        ValidationErrorMessage errorMessage = new ValidationErrorMessage(
                thrown.getClass().getSimpleName(),
                parameterErrorMessage(locale),
                parameterErrors);

        return errorMessage;
    }

    // @ReqestParameterに対するパラメータなしエラー
    public ValidationErrorMessage from(MissingServletRequestParameterException thrown, Locale locale) {

        String fieldName = thrown.getParameterName();
        String message = messageSource.getMessage(
                REQUEST_PARAMETER_NONE_MESSAGE,
                null,
                defaultLocale());

        MessageItem item = new MessageItem(fieldName, message);

        return new ValidationErrorMessage(
                thrown.getClass().getSimpleName(),
                parameterErrorMessage(locale),
                List.of(item));
    }

    // 入力のコンバートエラー
    public ValidationErrorMessage from(TypeMismatchException thrown, Locale locale) {

        String fieldName = thrown.getPropertyName();
        String requiredType = thrown.getRequiredType().getSimpleName();

        String message = messageSource.getMessage(
                CONVERT_ERROR_MESSAGE,
                new Object[] { requiredType },
                defaultLocale());

        MessageItem item = new MessageItem(fieldName, message);

        return new ValidationErrorMessage(
                thrown.getClass().getSimpleName(),
                parameterErrorMessage(locale),
                List.of(item));
    }


    // -------------------------------------------------------- private methods

    private String parameterErrorMessage(Locale locale) {
        return messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null, locale);
    }

    private String resovleFieldName(MessageSourceResolvable errorMessage, CodeType codeType, Locale locale) {

        // MessageSourceResolvable#getArgumentsの0番目はエラーとなったフィールド固定
        // https://terasolunaorg.github.io/guideline/current/ja/ArchitectureInDetail/WebApplicationDetail/Validation.html#application-messages-properties
        return switch (errorMessage.getArguments()[0]) {
            case MessageSourceResolvable fieldMessage -> messageSource.getMessage(
                    new SelectableDefaultMessageResolver(fieldMessage, codeType),
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
        if (array == null || array.length == 0) {
            return null;
        }
        return array[array.length - 1];
    }


    // -------------------------------------------------------- inner classes.

    @RequiredArgsConstructor
    static class SelectableDefaultMessageResolver implements MessageSourceResolvable {

        enum CodeType {
            LONG,
            DEFAULT
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
            return switch (type) {
                case LONG -> codes[0];
                case DEFAULT -> original.getDefaultMessage();
            };
        }
    }
}
