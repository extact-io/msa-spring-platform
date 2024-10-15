package io.extact.msa.spring.platform.core.debug;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "rms.debug.configdump")
public record ConfigDumpProperties(
        @DefaultValue("false") boolean enable,
        @DefaultValue("false") boolean systemProperties,
        @DefaultValue("false") boolean systemEnvironment,
        @DefaultValue Filter filter) {

    record Filter(
            @DefaultValue("true") boolean enable,
            @DefaultValue List<String> patterns) {
    }
}
