package io.extact.msa.spring.platform.fw.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.extact.msa.spring.platform.core.condition.SkipRegistration;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.exception.response.ErrorMessage;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorItem;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice(annotations = ExceptionHandled.class)
@SkipRegistration
@Slf4j
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String RMS_EXCEPTION_HEAD = "rms-exception";
    private static final String CONVERT_ERROR_MESSAGE = "ex.TypeMismatchException.massage";
    private static final String PARAMETER_ERROR_MESSAGE = "ex.ParameterErrorException.message";

    @ExceptionHandler(BusinessFlowException.class)
    public ResponseEntity<ErrorMessage> handleBusinessFlowException(BusinessFlowException e, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(e.getCauseType().name(), e.getMessage());
        HttpStatus status = switch (e.getCauseType()) {
            case NOT_FOUND          -> HttpStatus.NOT_FOUND;
            case DUPLICATE, REFERED -> HttpStatus.CONFLICT;
            case FORBIDDEN          -> HttpStatus.FORBIDDEN;
        };

        return ResponseEntity
                .status(status)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(errorMessage);
    }

    @ExceptionHandler(RmsServiceUnavailableException.class)
    public ResponseEntity<ErrorMessage> handleServiceUnavailableException(RmsServiceUnavailableException e,
            WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(errorMessage);
    }

    @ExceptionHandler(RmsSystemException.class)
    public ResponseEntity<ErrorMessage> handleRmsSystemException(RmsSystemException e, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(errorMessage);
    }

    // 入力のコンバートエラー
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        String fieldName = e.getPropertyName();
        String requiredType = e.getRequiredType().getSimpleName();

        String errorMessage = this.getMessageSource().getMessage(
                CONVERT_ERROR_MESSAGE,
                new Object[] { requiredType },
                req.getLocale());

        ValidationErrorItem errorItem = new ValidationErrorItem(fieldName, errorMessage);

        ValidationErrorMessage validationMessage = new ValidationErrorMessage(
                e.getClass().getSimpleName(),
                resolveParameterErrorMessage(req),
                List.of(errorItem));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, TypeMismatchException.class.getSimpleName())
                .body(validationMessage);
    }

    // @Validatedに対する入力チェックエラー
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        Stream<ValidationErrorItem> fieldErrors = e.getFieldErrors().stream().map(error -> {
            String fieldName = resovleFieldName(error, req.getLocale());
            String message = this.getMessageSource().getMessage(error, req.getLocale());
            message = this.getMessageSource().getMessage(error, req.getLocale());
            return (ValidationErrorItem) new ValidationErrorItem(fieldName, message);
        });

        Stream<ValidationErrorItem> globalErrors = e.getGlobalErrors().stream().map(error -> {
            String globalName = resovleObjectName(error, req.getLocale());
            String message = this.getMessageSource().getMessage(error, req.getLocale());
            return (ValidationErrorItem) new ValidationErrorItem(globalName, message);
        });

        ValidationErrorMessage validationMessage = new ValidationErrorMessage(
                e.getClass().getSimpleName(),
                resolveParameterErrorMessage(req),
                Stream.concat(fieldErrors, globalErrors).toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(validationMessage);
    }

    // @Validated以外(@NotNullなど)の入力チェックエラー
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        // MethodValidationResult -> ParameterValidationResult x n -> MessageSourceResolvable x n を1次元にflat化する
        List<MessageSourceResolvable> errors = e.getAllValidationResults().stream()
                .flatMap(validationResult -> validationResult.getResolvableErrors().stream())
                .toList();

        List<ValidationErrorItem> parameterErrors = errors.stream().map(error -> {
            String fieldName = resovleFieldName(error, req.getLocale());
            String message = this.getMessageSource().getMessage(error, req.getLocale());
            return (ValidationErrorItem) new ValidationErrorItem(fieldName, message);
        }).toList();

        ValidationErrorMessage validationMessage = new ValidationErrorMessage(
                e.getClass().getSimpleName(),
                resolveParameterErrorMessage(req),
                parameterErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(validationMessage);
    }

    private String resovleFieldName(MessageSourceResolvable errorMessage, Locale locale) {
        // MessageSourceResolvable#getArgumentsの0番目はエラーとなったフィールド固定
        // https://terasolunaorg.github.io/guideline/current/ja/ArchitectureInDetail/WebApplicationDetail/Validation.html#application-messages-properties
        return switch (errorMessage.getArguments()[0]) {
            case MessageSourceResolvable fieldMessage -> this.getMessageSource().getMessage(fieldMessage, locale);
            default -> "unknown field...";
        };
    }

    // 相関チェックなどのオブジェクトレベルのエラーのフィールドのデフォルトメッセージは空になることがあるためこれを補完する
    private String resovleObjectName(MessageSourceResolvable errorMessage, Locale locale) {

        return switch (errorMessage.getArguments()[0]) {

            case MessageSourceResolvable fieldMessage -> {

                if (StringUtils.hasText(fieldMessage.getDefaultMessage())) {
                    yield this.getMessageSource().getMessage(fieldMessage, locale);
                }

                DefaultMessageSourceResolvable objectName = new DefaultMessageSourceResolvable(
                        fieldMessage.getCodes(),
                        fieldMessage.getArguments(),
                        getLastElement(fieldMessage.getCodes())); // defaultMessage
                yield this.getMessageSource().getMessage(objectName, locale);
            }

            default -> "unknown field...";
        };
    }

    private String resolveParameterErrorMessage(WebRequest request) {
        return this.getMessageSource().getMessage(PARAMETER_ERROR_MESSAGE, null, request.getLocale());
    }

    private static <T> T getLastElement(T[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return null;
        }
        return array[array.length - 1];
    }
}
