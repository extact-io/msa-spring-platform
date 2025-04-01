package io.extact.msa.spring.platform.fw.feature.exception;

import io.extact.msa.spring.platform.fw.exception.RentalReservationServiceException;

/**
 * Controllerで自分でリクエストパラメーターをチェックしてエラーを検知したときに投げる例外
 */
public class RmsRequestCheckException extends RentalReservationServiceException {

    public RmsRequestCheckException(String message) {
        super(message);
    }
    
    public RmsRequestCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
