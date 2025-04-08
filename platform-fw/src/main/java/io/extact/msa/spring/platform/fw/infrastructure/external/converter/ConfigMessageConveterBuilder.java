package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMessageConveterBuilder {

    private final ExternalProperties prop;

    public static ConfigMessageConveterBuilder builder(ExternalProperties prop) {
        return new ConfigMessageConveterBuilder(prop);
    }

    public MappingJackson2HttpMessageConverter build() {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        prop.getOptinalDateFormat().ifPresent(fmt -> {
            module.addSerializer(LocalDate.class, new LocalDateSerializer(
                    DateTimeFormatter.ofPattern(fmt)));
            module.addDeserializer(LocalDate.class, new LocalDateDeserializer(
                    DateTimeFormatter.ofPattern(fmt)));
        });
        prop.getOptinalDateTimeFormat().ifPresent(fmt -> {
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                    DateTimeFormatter.ofPattern(fmt)));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                    DateTimeFormatter.ofPattern(fmt)));
        });

        mapper.registerModule(module);

        return new MappingJackson2HttpMessageConverter(mapper);
    }
}
