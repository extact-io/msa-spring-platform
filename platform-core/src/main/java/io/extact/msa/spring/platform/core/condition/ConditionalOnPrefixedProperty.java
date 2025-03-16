package io.extact.msa.spring.platform.core.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(PrefixedPropertyCondition.class)
public @interface ConditionalOnPrefixedProperty {

    @AliasFor("prefix")
    String value() default "";

    @AliasFor("value")
    String prefix() default "";
}
