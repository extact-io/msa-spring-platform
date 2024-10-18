package io.extact.msa.spring.platform.fw.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import io.extact.msa.spring.platform.fw.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidator;

@Documented
@Constraint(validatedBy = { BeforeAfterDateTimeValidator.class })
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface BeforeAfterDateTime {

    String message() default "{bv.BeforeAfterDateTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from() default "label.from.datetime";

    String to() default "label.to.datetime";

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        BeforeAfterDateTime[] value();
    }

    public static class BeforeAfterDateTimeValidator implements ConstraintValidator<BeforeAfterDateTime, BeforeAfterDateTimeValidatable> {
        public boolean isValid(BeforeAfterDateTimeValidatable bean, ConstraintValidatorContext context) {
            if (bean.fromDateTime() == null || bean.toDateTime() == null) {
                return true; // チェックしない
            }
            return bean.fromDateTime().isBefore(bean.toDateTime());
        }
    }

    public interface BeforeAfterDateTimeValidatable {
        public LocalDateTime fromDateTime();
        public LocalDateTime toDateTime();
    }
}
