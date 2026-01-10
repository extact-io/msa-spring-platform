package io.extact.msa.spring.platform.fw.feature.auth.jackson;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;

@Configuration(proxyBeanMethods = false)
public class RedisObjectMapperConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE) // default settings
    RedisObjectMapperCustomizer rmsLoginUserAttributesMapperCustomizer() {
        return builder -> {
            builder.mixIn(AuthUserId.class, IgnoreIsAnonymousMixIn.class);
        };
    }

    @Bean
    @RedisObjectMapper
    ObjectMapper redisObjectMapper(ApplicationContext context, List<RedisObjectMapperCustomizer> customizers) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.applicationContext(context);
        customizers.forEach(customizer -> customizer.customize(builder));
        return builder.createXmlMapper(false).build();
    }
}
