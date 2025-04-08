package io.extact.msa.spring.platform.fw.infrastructure.external;

import java.util.Optional;

import lombok.Data;

@Data
public class ExternalProperties {
    
    private String url;
    private FormatProperties format;

    @Data
    public static class FormatProperties {
        private String date;
        private String dateTime;
    }
    
    public Optional<FormatProperties> getOptionalFormat() {
        return Optional.ofNullable(format);
    }

    public Optional<String> getOptinalDateFormat() {
        return getOptionalFormat().map(FormatProperties::getDate);
    }

    public Optional<String> getOptinalDateTimeFormat() {
        return getOptionalFormat().map(FormatProperties::getDateTime);
    }
}
