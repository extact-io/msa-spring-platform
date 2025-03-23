package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.extact.msa.spring.platform.core.utils.EnvironmentUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMessageConveterBuilder {

    private final Environment env;

    public static ConfigMessageConveterBuilder builder(Environment env) {
        return new ConfigMessageConveterBuilder(env);
    }

    public MappingJackson2HttpMessageConverter build() {

        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();

        Optional<String> datePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.client.format.date");
        Optional<String> dateTimePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.rest.client.format.date-time");

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

        mapper.registerModule(module);

        return new MappingJackson2HttpMessageConverter(mapper);
    }
}
