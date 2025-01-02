package io.extact.msa.spring.platform.core.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnAnyEndsWithProfileCondition.class)
public @interface ConditionalOnAnyEndsWithProfile {
    // 有効と判定するプロファイルのリスト
    String[] value();
}
