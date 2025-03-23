package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.util.Optional;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;

import io.extact.msa.spring.platform.core.utils.EnvironmentUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigConversionServiceBuilder {

    private final Environment env;
    private Optional<ConfigurableConversionService> customizedService = Optional.empty();

    public static ConfigConversionServiceBuilder builder(Environment env) {
        return new ConfigConversionServiceBuilder(env);
    }

    public ConfigConversionServiceBuilder customizedService(ConfigurableConversionService customizedService) {
        this.customizedService = Optional.of(customizedService);
        return this;
    }

    public ConversionService build() {

        Optional<String> datePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.client.format.date");
        Optional<String> dateTimePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.client.format.date-time");

        ConfigurableConversionService baseService = customizedService
                .orElseGet(DefaultFormattingConversionService::new);

        datePattern.ifPresent(pttn -> {
            baseService.addConverter(new LocalDateToStringConverter(pttn));
        });
        dateTimePattern.ifPresent(pttn -> {
            baseService.addConverter(new LocalDateTimeToStringConverter(pttn));
        });

        return baseService;
    }
}
