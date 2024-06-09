package io.extact.msa.spring.platform.fw.auth.header;

import org.springframework.security.core.AuthenticationException;

public class InvalidUserIdHeaderException extends AuthenticationException {

    public InvalidUserIdHeaderException(String msg) {
        super(msg);
    }

    public InvalidUserIdHeaderException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
