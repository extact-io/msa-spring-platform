package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.data.redis.core.RedisTemplate;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisAttributesProvider implements LoginUserAttributesProvider<RmsLoginUserAttributes> {

    private final RedisTemplate<AuthUserId, RmsLoginUserAttributes> template;

    @Override
    public RmsLoginUserAttributes provide(AuthUserId userId) {
        return template.opsForValue().get(userId);
    }
}
