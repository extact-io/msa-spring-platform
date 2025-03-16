package io.extact.msa.spring.platform.core.condition;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import io.extact.msa.spring.platform.core.utils.EnvironmentUtils;

public class PrefixedPropertyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        MergedAnnotation<ConditionalOnPrefixedProperty> annotation =
                metadata.getAnnotations().get(ConditionalOnPrefixedProperty.class);

        String prefix = annotation.getString("prefix");
        Objects.requireNonNull(prefix);

        Environment env = context.getEnvironment();
        boolean match = EnvironmentUtils.containsPropertyWithPrefix(env, prefix);

        ConditionMessage message = match
                ? ConditionMessage.forCondition("@ConditionalOnPrefixedProperty")
                        .foundExactly("found prefixed property: " + prefix)
                : ConditionMessage.forCondition("@ConditionalOnLoggingLevel")
                        .didNotFind("not found prefixed property: " + prefix).items();

        return new ConditionOutcome(match, message);
    }
}
