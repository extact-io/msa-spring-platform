package io.extact.msa.spring.platform.core.condition;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnAnyEndsWithProfileCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        Map<String, Object> attributes = metadata
                .getAnnotationAttributes(ConditionalOnAnyEndsWithProfile.class.getName());
        if (attributes == null) {
            return ConditionOutcome.noMatch("No profiles specified in @OnAnyEndWithProfileCondition");
        }

        String[] profiles = (String[]) attributes.get("value");
        Environment environment = context.getEnvironment();

        // プロファイル名が前方一致するプロファイルが一つでも有効かを確認
        return Stream.of(profiles)
                .filter(profile -> this.matchesProfiles(environment, profile))
                .findAny()
                .map(profile -> ConditionOutcome.match(
                        "Profile matched: " + profile))
                .orElseGet(() -> ConditionOutcome.noMatch(
                        "None of the specified profiles are active: " + String.join(", ", profiles)));
    }

    private boolean matchesProfiles(Environment env, String profilePrefix) {
        return Stream.of(env.getActiveProfiles())
                .anyMatch(activeProfile -> activeProfile.endsWith(profilePrefix));
    }
}
