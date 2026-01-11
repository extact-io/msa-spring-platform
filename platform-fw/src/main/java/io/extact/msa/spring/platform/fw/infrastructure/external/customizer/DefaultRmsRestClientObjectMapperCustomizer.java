package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRmsRestClientObjectMapperCustomizer implements RmsRestClientObjectMapperCustomizer {

    private final ExternalProperties props;

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        JavaTimeModule module = new JavaTimeModule();

        props.getOptinalDateFormat().ifPresent(fmt -> {
            module.addSerializer(LocalDate.class, new LocalDateSerializer(
                    DateTimeFormatter.ofPattern(fmt)));
            module.addDeserializer(LocalDate.class, new LocalDateDeserializer(
                    DateTimeFormatter.ofPattern(fmt)));
        });
        props.getOptinalDateTimeFormat().ifPresent(fmt -> {
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                    DateTimeFormatter.ofPattern(fmt)));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                    DateTimeFormatter.ofPattern(fmt)));
        });

        builder.modules(module);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
