package io.extact.msa.spring.platform.fw.web;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
import io.extact.msa.spring.platform.fw.exception.response.SimpleErrorMessage;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidationErrorTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice(annotations = ExceptionHandled.class)
@SkipRegistration
@RequiredArgsConstructor
@Slf4j
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String RMS_EXCEPTION_HEAD = "rms-exception";

    private final ValidationErrorTranslator errorTranslator;

    @ExceptionHandler(BusinessFlowException.class)
    public ResponseEntity<SimpleErrorMessage> handleBusinessFlowException(BusinessFlowException e, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        SimpleErrorMessage message = new SimpleErrorMessage(e.getCauseType().name(), e.getMessage());
        HttpStatus status = switch (e.getCauseType()) {
            case NOT_FOUND          -> HttpStatus.NOT_FOUND;
            case DUPLICATE, REFERED -> HttpStatus.CONFLICT;
            case FORBIDDEN          -> HttpStatus.FORBIDDEN;
        };

        return ResponseEntity
                .status(status)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(message);
    }

    @ExceptionHandler(RmsServiceUnavailableException.class)
    public ResponseEntity<SimpleErrorMessage> handleServiceUnavailableException(RmsServiceUnavailableException e,
            WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        SimpleErrorMessage message = new SimpleErrorMessage(e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(message);
    }

    @ExceptionHandler(RmsSystemException.class)
    public ResponseEntity<SimpleErrorMessage> handleRmsSystemException(RmsSystemException e, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        SimpleErrorMessage message = new SimpleErrorMessage(e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(message);
    }

    // 入力のコンバートエラー
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ValidationErrorMessage message = errorTranslator.from(e, req.getLocale());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, TypeMismatchException.class.getSimpleName())
                .body(message);
    }

    // @Validatedに対する入力チェックエラー
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ValidationErrorMessage message = errorTranslator.from(e, req.getLocale());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(message);
    }

    // @Validated以外(@NotNullなど)の入力チェックエラー
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException e, HttpHeaders headers, HttpStatusCode status, WebRequest req) {

        log.warn("exception occured. message={}", e.getMessage());

        ValidationErrorMessage message = errorTranslator.from(e, req.getLocale());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(RMS_EXCEPTION_HEAD, e.getClass().getSimpleName())
                .body(message);
    }
}
