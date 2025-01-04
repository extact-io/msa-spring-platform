package io.extact.msa.spring.platform.fw.infrastructure.external;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

import io.extact.msa.spring.platform.fw.exception.RentalReservationServiceException;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;

public class SecurityConstraintException extends RentalReservationServiceException {

    private final transient ClientHttpResponse response;

    public SecurityConstraintException(ClientHttpResponse response) {
        super(resolveMessage(response));
        this.response = response;
    }

    public int getErrorStatus() {
        try {
            return response.getStatusCode().value();
        } catch (IOException e) {
            throw new RmsSystemException(e);
        }
    }

    private static String resolveMessage(ClientHttpResponse response) {
        try {
            return switch (response.getStatusCode().value()) {
                case 401 -> "認証エラー";
                case 403 -> "認可エラー";
                default -> "不明のエラー";
            };
        } catch (IOException e) {
            throw new RmsSystemException(e);
        }
    }
}
