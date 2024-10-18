package io.extact.msa.spring.platform.fw.external;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.SecurityConstraintException;
import io.extact.msa.spring.platform.fw.exception.response.SimpleErrorMessage;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;

public class RestClientErrorHandler implements ResponseErrorHandler {

    private static final String RMS_EXCEPTION_HEAD = "rms-exception";

    private final Map<String, Function<ClientHttpResponse, RuntimeException>> execptionHandlerMap;
    private final Map<Integer, Function<ClientHttpResponse, RuntimeException>> statusHandlerMap;

    private final Function<ClientHttpResponse, RuntimeException> fallbackHandler;

    private final ErrorMessageDeserializer deserializer;

    public RestClientErrorHandler(ErrorMessageDeserializer deserializer) {

        execptionHandlerMap = new HashMap<>();
        execptionHandlerMap.put(BusinessFlowException.class.getSimpleName(), this::throwBusinessFlowException);
        execptionHandlerMap.put(RmsServiceUnavailableException.class.getSimpleName(), this::throwRmsServiceUnavailableException);
        execptionHandlerMap.put(TypeMismatchException.class.getSimpleName(), this::throwRmsValidationException);
        execptionHandlerMap.put(MethodArgumentNotValidException.class.getSimpleName(), this::throwRmsValidationException);
        execptionHandlerMap.put(HandlerMethodValidationException.class.getSimpleName(), this::throwRmsValidationException);
        execptionHandlerMap.put(RmsSystemException.class.getSimpleName(), this::throwRmsSystemException);

        statusHandlerMap = new HashMap<>();
        statusHandlerMap.put(HttpStatus.UNAUTHORIZED.value(), this::throwSecurityConstraintException);
        statusHandlerMap.put(HttpStatus.FORBIDDEN.value(), this::throwSecurityConstraintException);

        fallbackHandler = this::fallbackHandler;

        this.deserializer = deserializer;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getHeaders().containsKey(RMS_EXCEPTION_HEAD)
                || statusHandlerMap.containsKey(response.getStatusCode().value());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        String className = response.getHeaders().getFirst(RMS_EXCEPTION_HEAD);
        int statusCode = response.getStatusCode().value();

        Function<ClientHttpResponse, RuntimeException> handler =
                getOptional(execptionHandlerMap, className)
                .or(() -> getOptional(statusHandlerMap, statusCode))
                .orElse(fallbackHandler);

        handler.apply(response);
    }

    private BusinessFlowException throwBusinessFlowException(ClientHttpResponse response) {
        SimpleErrorMessage error = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        CauseType causeType = CauseType.valueOf(error.errorReason());
        throw new BusinessFlowException(error.errorMessage(), causeType);
    }

    private RmsServiceUnavailableException throwRmsServiceUnavailableException(ClientHttpResponse response) {
        SimpleErrorMessage error = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsServiceUnavailableException(error.errorMessage());
    }

    private RmsValidationException throwRmsValidationException(ClientHttpResponse response) {
        ValidationErrorMessage error = deserializer.deserializeResponse(response, ValidationErrorMessage.class);
        throw new RmsValidationException(error.errorMessage(), error);
    }

    private RmsSystemException throwRmsSystemException(ClientHttpResponse response) {
        SimpleErrorMessage error = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsSystemException(error.errorMessage());
    }

    private SecurityConstraintException throwSecurityConstraintException(ClientHttpResponse response) {
        throw new SecurityConstraintException(response);
    }

    private RmsSystemException fallbackHandler(ClientHttpResponse response) {
        SimpleErrorMessage error = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsSystemException(error.errorMessage());
    }

    private <K, V> Optional<V> getOptional(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

}
