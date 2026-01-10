package io.extact.msa.spring.platform.fw.feature.json;

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

import io.extact.msa.spring.platform.core.utils.EnvironmentUtils;

/**
 * @Primaryで登録されるObjectMapperに対するcustomizerを定義するコンフィグ。
 * @PrimaryのObjectMapperが利用される機能は以下のとおり
 * <pre>
 * ・RESTサーバの正常レスポンスのbodyのシリアライズに利用するMapper
 * ・RESTサーバでセキュリティFilterエラーになった際のエラーレスポンスのbodyのシリアライズに利用するMapper
 * </pre>
 * @PathVariableと@RequestParamのフォーマットはSpring Bootのspring.mvc.format.*の設定で行っている。
 */
@Configuration(proxyBeanMethods = false)
public class PrimaryObjectMapperConfig {

    private Optional<String> datePattern;
    private Optional<String> dateTimePattern;

    PrimaryObjectMapperConfig(Environment env) {
        this.datePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.json.converter.date");
        this.dateTimePattern = EnvironmentUtils
                .getOptionalProperty(env, "rms.json.converter.date-time");
    }


    // --------------------------------------------- for JsonBinding

    @Bean
    Jackson2ObjectMapperBuilderCustomizer primaryObjectMapperCustomizer() {
        // ObjectMapperの他の設定は"spring.jackson.*"のとおりにJacksonAutoConfigurationで
        // 設定され、@PrimaryでBean登録される
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
