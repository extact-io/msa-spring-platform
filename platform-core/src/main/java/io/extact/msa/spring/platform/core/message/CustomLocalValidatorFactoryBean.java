package io.extact.msa.spring.platform.core.message;

import java.util.regex.Pattern;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import jakarta.validation.ConstraintViolation;

public class CustomLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

    /** プレースホルダー({n})が含まれているか */
    private static final Pattern CONTAINS_PLACEHOLDER_PATTERN =  Pattern.compile("\\{\\d+\\}");

    /**
     * BeanValidationで解決したメッセージをフォーマットする必要があるかを判定する。
     * {n}のプレースホルダはBeanValidationで置換されないため、defaultMessageにそのまま残っている。
     * これを{@link MessageSourceResolvable#getArguments()}で置換するかを決定する。
     * <p>
     * デフォルトでは{0}が含まれていないとフォーマットの必要なし(false)となるため、defaultMessageに
     * {n}のプレースホルダが1つでもあればフォーマットの必要ありとするようにオーバーライドしている。
     * <p>
     * この詳細仕様は{@link SpringValidatorAdapter#requiresMessageFormat()}のJavadocを参照。
     *
     * @param violation BeanValidationのチェックエラー情報
     * @return {@link MessageSourceResolvable#getArguments()}で置換する場合はtrue
     * @see SpringValidatorAdapter#requiresMessageFormat
     */
    @Override
    protected boolean requiresMessageFormat(ConstraintViolation<?> violation) {
        return CONTAINS_PLACEHOLDER_PATTERN.matcher(violation.getMessage()).find();
    }
}
