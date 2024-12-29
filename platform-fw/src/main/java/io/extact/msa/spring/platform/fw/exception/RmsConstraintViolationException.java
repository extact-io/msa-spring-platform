package io.extact.msa.spring.platform.fw.exception;

import java.util.Set;

import jakarta.validation.ConstraintViolation;

import io.extact.msa.spring.platform.fw.exception.RentalReservationServiceException;

public class RmsConstraintViolationException extends RentalReservationServiceException {

    private final Set<ConstraintViolation<?>> violations;

    public RmsConstraintViolationException(String message, Set<ConstraintViolation<?>> result) {
        super(message);
        this.violations = result;
    }
}
