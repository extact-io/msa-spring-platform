package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

class LocalDateToStringConverter implements Converter<LocalDate, String> {

    private final DateTimeFormatter formatter;

    public LocalDateToStringConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public String convert(LocalDate source) {
        return source.format(formatter);
    }
}
