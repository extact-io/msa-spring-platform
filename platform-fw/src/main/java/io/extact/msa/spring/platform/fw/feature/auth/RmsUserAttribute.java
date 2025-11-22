package io.extact.msa.spring.platform.fw.feature.auth;

import java.io.Serializable;

import io.extact.msa.spring.platform.core.auth.user.UserAttributes;

public record RmsUserAttribute(
        int id,
        String loginId,
        String userName,
        String phoneNumber) implements UserAttributes, Serializable {
}
