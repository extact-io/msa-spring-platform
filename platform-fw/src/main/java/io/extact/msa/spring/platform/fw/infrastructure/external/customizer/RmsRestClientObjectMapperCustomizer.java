package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
public interface RmsRestClientObjectMapperCustomizer {

    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);
}
