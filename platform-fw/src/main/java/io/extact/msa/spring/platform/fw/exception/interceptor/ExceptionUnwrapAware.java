package io.extact.msa.spring.platform.fw.exception.interceptor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Inherited
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface ExceptionUnwrapAware {
}
