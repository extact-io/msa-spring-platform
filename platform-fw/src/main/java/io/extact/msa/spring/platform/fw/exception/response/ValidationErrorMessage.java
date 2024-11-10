package io.extact.msa.spring.platform.fw.exception.response;

import java.util.List;
import java.util.stream.Collectors;

public record ValidationErrorMessage(
        SimpleErrorMessage validationErrorMessage,
        List<ValidationErrorItem> validationErrorItems) implements ErrorMessage {

    @Override
    public String errorMessage() {
        return validationErrorMessage.errorMessage();
    }

    @Override
    public String errorReason() {
        return validationErrorMessage.errorReason();
    }

    public String errorDetail() {
        return validationErrorItems.stream()
                .map(item -> "[%s:%s]".formatted(item.fieldName(), item.message()))
                .collect(Collectors.joining(","));
    }
}
