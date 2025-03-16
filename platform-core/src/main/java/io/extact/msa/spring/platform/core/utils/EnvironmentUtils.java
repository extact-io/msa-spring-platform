package io.extact.msa.spring.platform.core.utils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

public class EnvironmentUtils {

    @SuppressWarnings("unchecked")
    public static Set<String> getAllPropertyKeys(Environment env) {
        MutablePropertySources sources = ((ConfigurableEnvironment) env).getPropertySources();
        return sources.stream()
                .filter(source -> Map.class.isAssignableFrom(source.getSource().getClass()))
                .flatMap(source -> ((Map<String, ?>) source.getSource()).keySet().stream())
                .collect(Collectors.toSet());
    }

    public static boolean containsPropertyWithPrefix(Environment env, String prefix) {
        return getAllPropertyKeys(env).stream()
                .anyMatch(key -> key.startsWith(prefix));
    }

    public static Optional<String> getOptionalProperty(Environment env, String propKey) {
        String value = env.getProperty(propKey);
        return value == null ? Optional.empty() : Optional.of(value);
    }
}
