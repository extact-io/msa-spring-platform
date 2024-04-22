package io.extact.msa.spring.platform.fw.persistence.file.io;

import java.io.IOException;

import io.extact.msa.spring.platform.fw.exception.RmsSystemException;

/**
 * RMSで捕捉済みを意味するIO例外
 */
public class IoSystemException extends RmsSystemException {

    public IoSystemException(IOException cause) {
        super(cause);
    }

    public IoSystemException(String message, IOException cause) {
        super(message, cause);
    }
}
