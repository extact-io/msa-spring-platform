package io.extact.msa.spring.platform.fw.exception.response;

public record SimpleErrorMessage(
        String errorReason,
        String errorMessage) implements ErrorMessage {
}
