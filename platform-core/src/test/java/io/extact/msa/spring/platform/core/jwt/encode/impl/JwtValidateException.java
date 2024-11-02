package io.extact.msa.spring.platform.core.jwt.encode.impl;

public class JwtValidateException extends Exception {
    public JwtValidateException(Exception e) {
        super(e);
    }
}
