package io.extact.msa.spring.platform.core.debug;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.PlaceholderResolutionException;

import lombok.extern.slf4j.Slf4j;

/**
 * Dump out Config at startup.
 * Filter the output by setting configdump.filters as below.
 * <pre>
 * rms.debug:
 *   configdump:
 *     enable: false
 *     system-properties: off
 *     system-environment: off
 *     filter:
 *       enable: true
 *       patterns:
 *         - security
 *         - env.rms
 * </pre>
 */
@Slf4j(topic = "ConfigDump")
public class ConfigDump {

    private Environment env;
    private ConfigDumpProperties dumpProps;

    ConfigDump(Environment env, ConfigDumpProperties dumpConfig) {
        this.env = env;
        this.dumpProps = dumpConfig;
    }

    @PostConstruct
    void init() {

        if (!log.isDebugEnabled() || !dumpProps.enable()) {
            return;
        }

        Set<String> allPropertyNames = getAllPropertyNames();

        List<String> filters = Collections.emptyList();
        if (dumpProps.filter().enable()) {
            filters = dumpProps.filter().patterns();
        }

        Predicate<String> containsKeyword = new ContainsKeyworkWithForwardMatch(filters);
        String configDump = allPropertyNames.stream()
                .filter(containsKeyword)
                .map(name -> name + "=" + Optional.ofNullable(resolvePropertyValue(name)).orElse(""))
                .sorted()
                .collect(Collectors.joining(System.lineSeparator()));

        log.debug(System.lineSeparator() + configDump);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getAllPropertyNames() {
        MutablePropertySources sources = ((ConfigurableEnvironment) env).getPropertySources();
        return sources.stream()
                .filter(source -> Map.class.isAssignableFrom(source.getSource().getClass()))
                .filter(source -> {
                    if (StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                        return dumpProps.systemProperties();
                    }
                    if (StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                        return dumpProps.systemEnvironment();
                    }
                    return true;
                })
                .flatMap(source -> ((Map<String, ?>) source.getSource()).keySet().stream())
                .collect(Collectors.toSet());
    }

    private String resolvePropertyValue(String name) {
        try {
            return env.getProperty(name);
        } catch (PlaceholderResolutionException e) {
            log.info("PlaceHolder could not be resolved. name -> {} ", name);
            return getRawProperty(name);
        }

    }

    private String getRawProperty(String key) {
        if (!(env instanceof ConfigurableEnvironment configurableEnv)) {
            return "Error! PlaceHolder could not be resolved";
        }
        return configurableEnv.getPropertySources().stream()
                .map(propSource -> propSource.getProperty(key))
                .filter(Predicate.not(Objects::isNull))
                .findFirst()
                .map(Object::toString)
                .orElse(null);
    }

    static class ContainsKeyworkWithForwardMatch implements Predicate<String> {
        private List<String> filters;

        ContainsKeyworkWithForwardMatch(List<String> filters) {
            this.filters = filters;
        }

        @Override
        public boolean test(String name) {
            return filters.isEmpty() || filters.stream().anyMatch(name::startsWith);
        }
    }
}
