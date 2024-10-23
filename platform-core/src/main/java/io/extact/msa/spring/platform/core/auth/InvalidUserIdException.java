package io.extact.msa.spring.platform.core.auth;

import org.springframework.security.core.AuthenticationException;

public class InvalidUserIdException extends AuthenticationException {

    public InvalidUserIdException(String msg) {
        super(msg);
    }

    public InvalidUserIdException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
