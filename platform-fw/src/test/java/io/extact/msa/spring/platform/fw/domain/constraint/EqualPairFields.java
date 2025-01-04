package io.extact.msa.spring.platform.fw.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { EqualPairFields.PairFieldsEqualsValidator.class })
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface EqualPairFields {

    String message() default "{bv.PairFieldsEquals.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String val1() default "input1";

    String val2() default "input2";

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        EqualPairFields[] value();
    }

    public static class PairFieldsEqualsValidator
            implements ConstraintValidator<EqualPairFields, EqualPairFields.EqualPairFieldsValidatable> {

        public boolean isValid(EqualPairFields.EqualPairFieldsValidatable bean, ConstraintValidatorContext context) {
            if (bean.val1() == null || bean.val2() == null) {
                return true; // チェックしない
            }
            return bean.val1().equals(bean.val2());
        }
    }

    public interface EqualPairFieldsValidatable {
        String val1();

        String val2();
    }
}