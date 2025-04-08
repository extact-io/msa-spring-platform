package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.util.Optional;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigConversionServiceBuilder {

    private final ExternalProperties prop;
    private Optional<ConfigurableConversionService> customizedService = Optional.empty();

    public static ConfigConversionServiceBuilder builder(ExternalProperties prop) {
        return new ConfigConversionServiceBuilder(prop);
    }

    public ConfigConversionServiceBuilder customizedService(ConfigurableConversionService customizedService) {
        this.customizedService = Optional.of(customizedService);
        return this;
    }

    public ConversionService build() {

        ConfigurableConversionService baseService = customizedService
                .orElseGet(DefaultFormattingConversionService::new);

        prop.getOptinalDateFormat().ifPresent(fmt -> {
            baseService.addConverter(new LocalDateToStringConverter(fmt));
        });
        prop.getOptinalDateTimeFormat().ifPresent(fmt -> {
            baseService.addConverter(new LocalDateTimeToStringConverter(fmt));
        });

        return baseService;
    }
}
