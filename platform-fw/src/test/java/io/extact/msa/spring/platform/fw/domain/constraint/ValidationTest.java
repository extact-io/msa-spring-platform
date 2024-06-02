package io.extact.msa.spring.platform.fw.domain.constraint;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

/**
 * Integration tests for {@link MessageSourceMessageInterpolator}.
 *
 * @author Dmytro Nosan
 */
class ValidationTest {

    @NotNull
    private String defaultMessage;

    @NotNull(message = "{null}")
    private String nullable;

    @NotNull(message = "{blank}")
    private String blank;

    @NotNull(message = "{unknown}")
    private String unknown;

    @NotNull(message = "{recursion}")
    private String recursion;

    @NotNull(message = "\\{null}")
    private String escapePrefix;

    @NotNull(message = "{null\\}")
    private String escapeSuffix;

    @NotNull(message = "\\{null\\}")
    private String escapePrefixSuffix;

    @NotNull(message = "\\\\{null}")
    private String escapeEscape;

    @lombok.Data
    static class Data {
        //@NotNull
        private String defaultMessage;

        @NotNull(message = "{sample.message}")
        private String nullable;
    }

    @Test
    void defaultMessage() {
        assertThat(validate("defaultMessage")).containsExactly("must not be null");
    }

    @Test
    void nullable() {
        assertThat(validate("nullable")).containsExactly("must not be null");
    }

    @Test
    void blank() {
        assertThat(validate("blank")).containsExactly("must not be null or must not be blank");
    }

    @Test
    void recursion() {
        assertThatException().isThrownBy(() -> validate("recursion"))
            .withStackTraceContaining("Circular reference '{recursion -> middle -> recursion}'");
    }

    @Test
    void unknown() {
        assertThat(validate("unknown")).containsExactly("{unknown}");
    }

    @Test
    void escapePrefix() {
        assertThat(validate("escapePrefix")).containsExactly("\\{null}");
    }

    @Test
    void escapeSuffix() {
        assertThat(validate("escapeSuffix")).containsExactly("{null\\}");
    }

    @Test
    void escapePrefixSuffix() {
        assertThat(validate("escapePrefixSuffix")).containsExactly("{null}");
    }

    @Test
    void escapeEscape() {
        assertThat(validate("escapeEscape")).containsExactly("\\must not be null");
    }

    @Test
    void entityTest() {

        // Validatorに定義されているmessageIdをもとにmessageResourceが優先されメッセージを取得
        // なければBeanValidatorがメッセージ定義から取得が行われる
        // NotNullとかその状況に優先したメッセージ解決したい場合は自分でvalidateしている場合は自分で
        // MessageSource.getMessage(MessageSourceResolvable)する必要がある

        org.springframework.validation.Validator validator = (org.springframework.validation.Validator)builSpringdValidator();

        MessageSource messageSource = messageSource();

        Errors errors = validator.validateObject(new Data());
        errors.getAllErrors().forEach(error -> {
            String message = messageSource.getMessage(error, Locale.getDefault());
            System.out.println(error);
            System.out.println(error.getDefaultMessage());
            System.out.println(message);
        });

//        Validator v = buildValidator();
//        Set<ConstraintViolation<Data>> constraints = v.validate(new Data());
//        constraints.forEach(System.out::println);

    }

    private static MessageSource messageSource() {
        Locale locale = LocaleContextHolder.getLocale();
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("blank", locale, "{null} or {jakarta.validation.constraints.NotBlank.message}");
        messageSource.addMessage("null", locale, "{jakarta.validation.constraints.NotNull.message}");
        messageSource.addMessage("recursion", locale, "{middle}");
        messageSource.addMessage("middle", locale, "{recursion}");
        messageSource.addMessage("sample.message", locale, "{0}はヌルはダメ");
        messageSource.addMessage("NotNull", locale, "{0}はnullはダメ");
        messageSource.addMessage("nullable", locale, "項目");
        return messageSource;
    }

    private List<String> validate(String property) {
        return withEnglishLocale(() -> {
            Validator validator = buildValidator();
            List<String> messages = new ArrayList<>();
            Set<ConstraintViolation<Object>> constraints = validator.validateProperty(this, property);
            for (ConstraintViolation<Object> constraint : constraints) {
                messages.add(constraint.getMessage());
            }
            return messages;
        });
    }

    private static Validator buildValidator() {
        MessageSource messageSource = messageSource();
        MessageInterpolatorFactory messageInterpolatorFactory = new MessageInterpolatorFactory(messageSource);
        try (LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean()) {
            validatorFactory.setMessageInterpolator(messageInterpolatorFactory.getObject());
            validatorFactory.afterPropertiesSet();
            return validatorFactory.getValidator();
        }
    }

    private static org.springframework.validation.Validator builSpringdValidator() {
        MessageSource messageSource = messageSource();
        MessageInterpolatorFactory messageInterpolatorFactory = new MessageInterpolatorFactory(messageSource);
        try (LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean()) {
            validatorFactory.setMessageInterpolator(messageInterpolatorFactory.getObject());
            validatorFactory.afterPropertiesSet();
            return validatorFactory;
        }
    }

    private static <T> T withEnglishLocale(Supplier<T> supplier) {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            return supplier.get();
        }
        finally {
            Locale.setDefault(defaultLocale);
        }
    }

}