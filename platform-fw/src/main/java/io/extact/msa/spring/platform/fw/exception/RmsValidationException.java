package io.extact.msa.spring.platform.fw.exception;

import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;

public class RmsValidationException extends RentalReservationServiceException {

    private ValidationErrorMessage validationError;

    public RmsValidationException(ValidationErrorMessage validationError) {
        super(validationError.errorMessage());
        this.validationError = validationError;
    }

    public RmsValidationException(String message, ValidationErrorMessage validationError) {
        super(message);
        this.validationError = validationError;
    }

    public ValidationErrorMessage getErrorMessage() {
        return validationError;
    }

    public String getDetailMessage() {
        return validationError.errorDetail();
    }
}
