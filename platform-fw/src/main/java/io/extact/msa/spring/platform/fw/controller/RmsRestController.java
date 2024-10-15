package io.extact.msa.spring.platform.fw.controller;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.extact.msa.spring.platform.core.condition.SkipRegistration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@SkipRegistration
@ExceptionHandled
@RequestMapping
public @interface RmsRestController {

    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};
}
