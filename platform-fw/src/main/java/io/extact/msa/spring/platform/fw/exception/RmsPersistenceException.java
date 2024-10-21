package io.extact.msa.spring.platform.fw.exception;

public class RmsPersistenceException extends RmsSystemException {

    public RmsPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RmsPersistenceException(String message) {
        super(message);
    }

    public RmsPersistenceException(Throwable cause) {
        super(cause);
    }
}
