package io.extact.msa.spring.platform.core.jwt.provider;

public interface JsonWebTokenGenerator {
    String generateToken(UserClaims userClaims);
}