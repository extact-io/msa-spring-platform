package io.extact.msa.spring.platform.fw.exception.response;

import java.util.ArrayList;
import java.util.List;

public record ValidationErrorMessage(
        SimpleErrorMessage validationErrorMessage,
        List<ValidationErrorItem> validationErrorItems) implements ErrorMessage {

    @Override
    public List<ValidationErrorItem> validationErrorItems() {
        return new ArrayList<>(validationErrorItems);
    }

    @Override
    public String errorMessage() {
        return validationErrorMessage.errorMessage();
    }

    @Override
    public String errorReason() {
        return validationErrorMessage.errorReason();
    }
}
