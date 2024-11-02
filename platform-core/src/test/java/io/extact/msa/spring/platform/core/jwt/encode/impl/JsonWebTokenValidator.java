package io.extact.msa.spring.platform.core.jwt.encode.impl;

import io.extact.msa.spring.platform.core.jwt.encode.JsonWebToken;

public interface JsonWebTokenValidator {

    static final String TEST_PUBLIC_KEY_PATH = "/jwt.pub.key";

    JsonWebToken validate(String token) throws JwtValidateException;
}
