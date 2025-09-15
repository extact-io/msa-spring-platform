package io.extact.msa.spring.test.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Spring Security と actuatorを除外する。
 * actuatorがクラスパス上に存在すると設定でOFFにできないことに加え、actuatorはSpring Securityを
 * 要求する。このため、actuatorがクラスパス上にある状態でSpring Securityを除外したい場合は、
 * 一緒にactuatorも除外する必要がある
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableAutoConfiguration(exclude = {
        ManagementWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class })
public @interface EnableAutoConfigurationWithoutSecurityAndActuator {
}
