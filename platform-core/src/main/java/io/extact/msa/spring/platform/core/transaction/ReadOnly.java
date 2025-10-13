package io.extact.msa.spring.platform.core.transaction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Transactional;

/**
 * read-onlyなことを示す簡易的なメタアノテーション。
 * <code>@Transactional</code>の他の属性を変更したい場合はこのアノテーションを使わず
 * <code>@Transactional</code>を直接使用すること
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(readOnly = true)
public @interface ReadOnly {
}
