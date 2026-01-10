package io.extact.msa.spring.platform.fw.feature.auth.jackson;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@FunctionalInterface
public interface RedisObjectMapperCustomizer {

    void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);
}
