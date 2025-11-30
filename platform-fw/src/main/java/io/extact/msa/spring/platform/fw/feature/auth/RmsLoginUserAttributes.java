package io.extact.msa.spring.platform.fw.feature.auth;

import java.io.Serializable;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributes;

public record RmsLoginUserAttributes(
        AuthUserId authUserId,
        String attr1,
        String attr2,
        String attr3) implements LoginUserAttributes, Serializable {
}
