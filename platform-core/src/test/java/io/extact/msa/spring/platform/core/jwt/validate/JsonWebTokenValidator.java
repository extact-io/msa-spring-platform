package io.extact.msa.spring.platform.core.jwt.validate;

import io.extact.msa.spring.platform.core.jwt.JsonWebToken;
import io.extact.msa.spring.platform.core.jwt.JwtValidateException;

public interface JsonWebTokenValidator {

    static final String TEST_PUBLIC_KEY_PATH = "/jwt.pub.key";

    JsonWebToken validate(String token) throws JwtValidateException;
}
