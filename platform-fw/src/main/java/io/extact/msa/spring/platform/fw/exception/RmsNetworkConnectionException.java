package io.extact.msa.spring.platform.fw.exception;

public class RmsNetworkConnectionException extends RmsServiceUnavailableException {

    public RmsNetworkConnectionException(Throwable e) {
        super(e);
    }

    public RmsNetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
