package io.extact.msa.spring.platform.core.debug;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "rms.debug.configdump")
@Data
public class ConfigDumpProperties {

    private boolean enable = false;
    private boolean systemProperties = false;
    private boolean systemEnvironment = false;
    private Filter filter = new Filter();

    @Data
    static class Filter {
        private boolean enable = true;
        private List<String> pattern = new ArrayList<>();
    }
}
