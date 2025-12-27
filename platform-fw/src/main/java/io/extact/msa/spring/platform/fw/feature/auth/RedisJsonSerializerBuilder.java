package io.extact.msa.spring.platform.fw.feature.auth;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisJsonSerializerBuilder {

    private final ObjectMapper mapper;

    private Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();
    private Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();

    public static RedisJsonSerializerBuilder defaultMappper() {
        return new RedisJsonSerializerBuilder(new ObjectMapper());
    }

    public RedisJsonSerializerBuilder mappper(ObjectMapper mapper) {
        return new RedisJsonSerializerBuilder(mapper);
    }

    public <T> RedisJsonSerializerBuilder addSerializer(Class<? extends T> clazz, JsonSerializer<T> serializer) {
        serializers.put(clazz, serializer);
        return this;
    }

    public <T> RedisJsonSerializerBuilder addDeserializer(Class<? extends T> clazz, JsonDeserializer<T> deserializer) {
        deserializers.put(clazz, deserializer);
        return this;
    }

    public RedisJsonSerializerBuilder defaultSettings() {
        return null;
    }

    public RedisJsonSerializerBuilder build() {
        return null;
    }
}
