package io.extact.msa.spring.platform.fw.feature.profile;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import io.extact.msa.spring.platform.core.env.ActiveProfileResolver;

public class OnAnyPersistenceProfileCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        Map<String, Object> attributes = metadata
                .getAnnotationAttributes(ConditionalOnAnyPersistenceProfile.class.getName());
        if (attributes == null) {
            return ConditionOutcome.noMatch("No persistence type in @OnAnyEndWithProfileCondition");
        }

        PersistenceProfileType persistenceType = (PersistenceProfileType) attributes.get("value");
        Environment environment = context.getEnvironment();
        ActiveProfileResolver apr = new ActiveProfileResolver(environment);

        // 指定された永続化タイプに一致するプロファイルが1つでもあるか確認
        return Stream.of(apr.resolveActiveProfiles())
                .map(PersistenceActiveProfile::new)
                .filter(activeProfile -> activeProfile.matchesType(persistenceType))
                .findFirst()
                .map(activeProfile -> ConditionOutcome.match(
                        persistenceType.name() + " type profile matched: " + activeProfile.getProfileName()))
                .orElseGet(() -> ConditionOutcome.noMatch(
                        "None of " +  persistenceType.name() + " type profile are active: "
                                + String.join(", ", environment.getActiveProfiles())));
    }
}
