package io.extact.msa.spring.platform.core.stopbugs;

public @interface SuppressFBWarnings {

    String[] value() default {};

    String justification() default "";
}
