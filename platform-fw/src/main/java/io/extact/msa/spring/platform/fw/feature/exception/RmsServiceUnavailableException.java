package io.extact.msa.spring.platform.fw.feature.exception;

import io.extact.msa.spring.platform.fw.exception.RentalReservationServiceException;

public class RmsServiceUnavailableException extends RentalReservationServiceException {

    public RmsServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RmsServiceUnavailableException(String message) {
        super(message);
    }

    public RmsServiceUnavailableException(Throwable cause) {
        super(cause);
    }
}
