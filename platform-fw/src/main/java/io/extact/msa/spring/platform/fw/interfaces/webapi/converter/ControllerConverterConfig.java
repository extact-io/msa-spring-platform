package io.extact.msa.spring.platform.fw.interfaces.webapi.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.extact.msa.spring.platform.core.condition.ConditionalOnPrefixedProperty;
import io.extact.msa.spring.platform.core.utils.EnvironmentUtils;

/**
 * RestControllerで使用する日付フォーマットの設定。
 * <code>@PathVariable</code>と<code>@RequestParam</code>のフォーマットはSpring Bootの
 * <code>spring.mvc.format.*</code>の設定で行っている。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnPrefixedProperty("rms.rest.controller.format")
public class ControllerConverterConfig {

    private Optional<String> datePattern;
    private Optional<String> dateTimePattern;

    ControllerConverterConfig(Environment env) {
        this.datePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.controller.format.date");
        this.dateTimePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.controller.format.date-time");
    }


    // --------------------------------------------- for JsonBinding

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            JavaTimeModule module = new JavaTimeModule();

            datePattern.ifPresent(pattern -> {
                module.addSerializer(LocalDate.class, new LocalDateSerializer(
                        DateTimeFormatter.ofPattern(pattern)));
                module.addDeserializer(LocalDate.class, new LocalDateDeserializer(
                        DateTimeFormatter.ofPattern(pattern)));
            });

            dateTimePattern.ifPresent(pattern -> {
                module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                        DateTimeFormatter.ofPattern(pattern)));
                module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                        DateTimeFormatter.ofPattern(pattern)));
            });

            builder.modules(module);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}