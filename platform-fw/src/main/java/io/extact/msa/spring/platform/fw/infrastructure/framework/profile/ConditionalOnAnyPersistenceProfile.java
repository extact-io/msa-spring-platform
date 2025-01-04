package io.extact.msa.spring.platform.fw.infrastructure.framework.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnAnyPersistenceProfileCondition.class)
public @interface ConditionalOnAnyPersistenceProfile {
    PersistenceProfileType value();
}
