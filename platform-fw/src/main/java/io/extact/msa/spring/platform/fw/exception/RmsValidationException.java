package io.extact.msa.spring.platform.fw.exception;

import io.extact.msa.spring.platform.fw.exception.message.ValidationErrorMessage;

public class RmsValidationException extends RentalReservationServiceException {

    private ValidationErrorMessage errorMessage;

    public RmsValidationException(ValidationErrorMessage errorMessage) {
        super(errorMessage.errorMessage());
        this.errorMessage = errorMessage;
    }

    public ValidationErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public String getDetailMessage() {
        return errorMessage.errorDetail();
    }

    @Override
    public String getMessage() {
        return errorMessage.errorMessage() + System.lineSeparator() + errorMessage.errorDetail();
    }
}
