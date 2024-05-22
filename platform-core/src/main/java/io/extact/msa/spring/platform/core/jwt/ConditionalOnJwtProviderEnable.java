package io.extact.msa.spring.platform.core.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix = "rms.jwt-provider", name = "enable", havingValue = "true")
public @interface ConditionalOnJwtProviderEnable {
}
