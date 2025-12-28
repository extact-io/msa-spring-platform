package io.extact.msa.spring.platform.fw.feature.auth.jackson;

import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.fw.feature.auth.RmsLoginUserAttributes;

public class RmsLoginUserAttributesSerializer extends Jackson2JsonRedisSerializer<RmsLoginUserAttributes> {

    public RmsLoginUserAttributesSerializer() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(AuthUserId.class, IgnoreIsAnonymousMixIn.class);

        super(mapper, RmsLoginUserAttributes.class);
    }

    /**
     * {@link AuthUserId#isAnonymous()} が anonymousプロパティとしてシリアライズ
     * されないようにする。AuthUserIdがJacksonに直接依存するのを避けるためMixinに
     * している。
     */
    static abstract class IgnoreIsAnonymousMixIn {
        @JsonIgnore
        abstract boolean isAnonymous();
    }
}
