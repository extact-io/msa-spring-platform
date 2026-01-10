package io.extact.msa.spring.platform.fw.feature.auth.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;

/**
 * {@link AuthUserId#isAnonymous()} が anonymousプロパティとしてシリアライズ
 * されないようにする。AuthUserIdがJacksonに直接依存するのを避けるためMixinに
 * している。
 */
public abstract class IgnoreIsAnonymousMixIn {
    @JsonIgnore
    abstract boolean isAnonymous();
}