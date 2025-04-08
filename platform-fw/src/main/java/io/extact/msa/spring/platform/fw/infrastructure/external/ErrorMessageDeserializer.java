package io.extact.msa.spring.platform.fw.infrastructure.external;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.extact.msa.spring.platform.fw.exception.message.ErrorMessage;
import io.extact.msa.spring.platform.fw.exception.message.SimpleErrorMessage;
import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorMessageDeserializer {

    private final ObjectMapper objectMapper;

    public ErrorMessageDeserializer() {
        this(new ObjectMapper());
    }

    public ErrorMessageDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T extends ErrorMessage> T deserializeResponse(ClientHttpResponse response, Class<T> responseType) {
        String json = readBodyAsString(response);
        try {
            return objectMapper.readValue(json, responseType);
        } catch (Exception e) {
            log.warn(e.getMessage() + " -> " + json, e);
            return fallbackDeserialize(json, responseType, e);
        }
    }

    private String readBodyAsString(ClientHttpResponse response) {
        byte[] body = new byte[0];
        try (InputStream in = response.getBody()) {
            body = in.readAllBytes();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return new String(body, resolveCharset(response));
    }

    private Charset resolveCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null && contentType.getCharset() != null)
                ? contentType.getCharset()
                : StandardCharsets.UTF_8;
    }

    private <T extends ErrorMessage> T fallbackDeserialize(String json, Class<T> responseType, Exception e) {

        String reason = e.getClass().getSimpleName();
        String message = "json deserialize error. message:{%s} -> %s".formatted(e.getMessage(), json);

        ErrorMessage errorMessage;
        if (ValidationErrorMessage.class.isAssignableFrom(responseType)) {
            errorMessage = new ValidationErrorMessage(reason, message, Collections.emptyList());
        } else if (SimpleErrorMessage.class.isAssignableFrom(responseType)) {
            errorMessage = new SimpleErrorMessage(reason, message);
        } else {
            errorMessage = new SimpleErrorMessage(reason, message);
        }

        return responseType.cast(errorMessage);
    }
}
