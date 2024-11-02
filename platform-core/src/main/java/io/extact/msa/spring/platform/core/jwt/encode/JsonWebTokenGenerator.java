package io.extact.msa.spring.platform.core.jwt.encode;

public interface JsonWebTokenGenerator {

    String generateToken(UserClaims userClaims);
}