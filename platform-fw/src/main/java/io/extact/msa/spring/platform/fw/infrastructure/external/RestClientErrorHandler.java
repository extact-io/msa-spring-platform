package io.extact.msa.spring.platform.fw.infrastructure.external;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.exception.message.SimpleErrorMessage;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.feature.exception.RmsRequestCheckException;
import io.extact.msa.spring.platform.fw.feature.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.platform.fw.feature.exception.RmsValidationException;

public class RestClientErrorHandler implements ResponseErrorHandler {

    private static final String RMS_EXCEPTION_HEAD = "rms-exception";

    private final Map<String, Consumer<ClientHttpResponse>> execptionHandlerMap;
    private final Map<Integer, Consumer<ClientHttpResponse>> statusHandlerMap;

    private final Consumer<ClientHttpResponse> fallbackHandler;

    private final ErrorMessageDeserializer deserializer;

    public RestClientErrorHandler(ErrorMessageDeserializer deserializer) {

        execptionHandlerMap = new HashMap<>();
        execptionHandlerMap.put(BusinessFlowException.class.getSimpleName(), this::throwBusinessFlowException);
        execptionHandlerMap.put(RmsServiceUnavailableException.class.getSimpleName(), this::throwRmsServiceUnavailableException);
        execptionHandlerMap.put(RmsRequestCheckException.class.getSimpleName(), this::throwRmsRequestCheckException);
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
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {

        String className = response.getHeaders().getFirst(RMS_EXCEPTION_HEAD);
        int statusCode = response.getStatusCode().value();

        Consumer<ClientHttpResponse> handler = getOptional(execptionHandlerMap, className)
                .or(() -> getOptional(statusHandlerMap, statusCode))
                .orElse(fallbackHandler);

        handler.accept(response);
    }

    private void throwBusinessFlowException(ClientHttpResponse response) {
        SimpleErrorMessage body = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        CauseType causeType = CauseType.valueOf(body.errorReason());
        throw new BusinessFlowException(body.errorMessage(), causeType);
    }

    private void throwRmsServiceUnavailableException(ClientHttpResponse response) {
        SimpleErrorMessage body = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsServiceUnavailableException(body.errorMessage());
    }
    
    private void throwRmsRequestCheckException(ClientHttpResponse response) {
        SimpleErrorMessage body = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsRequestCheckException(body.errorMessage());
    }

    private void throwRmsValidationException(ClientHttpResponse response) {
        ValidationErrorMessage body = deserializer.deserializeResponse(response, ValidationErrorMessage.class);
        throw new RmsValidationException(body);
    }

    private void throwRmsSystemException(ClientHttpResponse response) {
        SimpleErrorMessage body = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsSystemException(body.errorMessage());
    }

    private void throwSecurityConstraintException(ClientHttpResponse response) {
        throw new SecurityConstraintException(response);
    }

    private void fallbackHandler(ClientHttpResponse response) {
        SimpleErrorMessage body = deserializer.deserializeResponse(response, SimpleErrorMessage.class);
        throw new RmsSystemException(body.errorMessage());
    }

    private <K, V> Optional<V> getOptional(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

}
