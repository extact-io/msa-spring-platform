package io.extact.msa.spring.platform.fw.feature.auth;

import java.io.Serializable;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.UserAttributes;

public record RmsUserAttribute(
        AuthUserId authUserId,
        String loginId,
        String userName,
        String phoneNumber) implements UserAttributes, Serializable {
}
