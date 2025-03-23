package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeToStringConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public String convert(LocalDateTime source) {
        return source.format(formatter);
    }
}
