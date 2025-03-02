package io.extact.msa.spring.platform.fw.exception.message;

public record SimpleErrorMessage(
        String errorReason,
        String errorMessage) implements ErrorMessage {
}
