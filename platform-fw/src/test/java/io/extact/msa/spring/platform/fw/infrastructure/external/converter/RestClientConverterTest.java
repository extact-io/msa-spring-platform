package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.platform.fw.infrastructure.external.CustomUriBuilderFactory;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConverterClientApi.DateTypeDto;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConverterClientApi.StringTypeDto;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsProxyFactorySourceCreator;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizer;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsRestClientCustomizerContext;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SimpleRestClientCustomizerConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestClientConverterTest {

    private static DateTimeFormatter dateFormatter;
    private static DateTimeFormatter dateTimeFormatter;

    @Autowired
    private RestClient client;

    @Configuration(proxyBeanMethods = false)
    @Import(SimpleRestClientCustomizerConfig.class)
    @EnableAutoConfigurationWithoutJpa
    static class TestConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable())
                    .build();
        }

        @Bean
        ConverterClientApiController converterClientApiController() {
            return new ConverterClientApiController();
        }

        @Bean
        @ConfigurationProperties("rms.persistence.person.remote")
        ExternalProperties externalProperties() {
            return new ExternalProperties();
        }

        @Bean
        RmsRestClientCustomizer overrideUriBuilderFactory(Environment env) {
            return new RmsRestClientCustomizer() {
                private ConversionService applied;
                @Override
                public void customize(Builder builder, RmsRestClientCustomizerContext context) {
                    CustomUriBuilderFactory.Builder uriBuilder = CustomUriBuilderFactory.newInstance()
                            .env(env)
                            .uriTemplate("http://localhost:${local.server.port}/converter");
                    context.currentAppiedConversionService().ifPresent(service -> {
                        uriBuilder.conversionService(service);
                        applied = service;
                    });
                    builder.uriBuilderFactory(uriBuilder.build());
                }
                @Override
                public ConversionService appliedConversionService() {
                    return applied;
                }
            };
        }

        @Bean
        RestClient converterClientApi(
                RestClient.Builder builder, // RestClientAutoConfigurationでCustomierが提供済みのBuilderを使用する
                List<RmsRestClientCustomizer> customizers) {
            RmsProxyFactorySourceCreator creator = new RmsProxyFactorySourceCreator(builder, customizers);
            return creator.create().restClient();
        }
    }

    @BeforeAll
    static void beforeAll(
            @Value("${rms.persistence.person.remote.format.date}") String datePattern,
            @Value("${rms.persistence.person.remote.format.date-time}") String dateTimePattern) {
        dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
    }

    @Test
    void testPathLocalDate() {
        // given
        String value = "20250305";
        LocalDate date = LocalDate.parse(value, dateFormatter);
        // when
        String actual = client
                .get()
                .uri("/date/{date}", date)
                .retrieve()
                .toEntity(String.class)
                .getBody();
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testPathLocalDateTime() {
        // given
        String value = "20250305 10:20";
        LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);
        // when
        String actual = client
                .get()
                .uri("/datetime/{datetime}", dateTime)
                .retrieve()
                .toEntity(String.class)
                .getBody();
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testParamLocalDate() {
        // given
        String value = "20250305";
        LocalDate date = LocalDate.parse(value, dateFormatter);
        // when
        String actual = client
                .get()
                .uri("/date", builder -> builder.queryParam("date", date).build())
                .retrieve()
                .toEntity(String.class)
                .getBody();
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testParamLocalDateTime() {
        // given
        String value = "20250305 10:20";
        LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);
        // when
        String actual = client
                .get()
                .uri("/datetime", builder -> builder.queryParam("datetime", dateTime).build())
                .retrieve()
                .toEntity(String.class)
                .getBody();
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testReturnLocalDate() {
        // given
        String value = "20250305";
        // when
        LocalDate actual = client
                .get()
                .uri("/return/date", builder -> builder.queryParam("date", value).build())
                .retrieve()
                .body(LocalDate.class); // 単項目だがJSON deserializeされる
        // then
        LocalDate expected = LocalDate.parse(value, dateFormatter);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testReturnLocalDateTime() {
        // given
        String value = "20250305 10:20";
        // when
        LocalDateTime actual = client
                .get()
                .uri("/return/datetime", builder -> builder.queryParam("datetime", value).build())
                .retrieve()
                .body(LocalDateTime.class); // 単項目だがJSON deserializeされる
        // then
        LocalDateTime expected = LocalDateTime.parse(value, dateTimeFormatter);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testSerializeDto() {
        // given
        String dateValue = "20250305";
        String dateTimeValue = "20250305 10:20";
        DateTypeDto dto = new DateTypeDto(
                LocalDate.parse(dateValue, dateFormatter),
                LocalDateTime.parse(dateTimeValue, dateTimeFormatter));
        // when
        StringTypeDto actual = client
                .post()
                .uri("/serialize")
                .body(dto)
                .retrieve()
                .body(StringTypeDto.class);
        // then
        StringTypeDto expected = new StringTypeDto(dateValue, dateTimeValue);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testDeserializeDto() {
        // given
        String dateValue = "20250305";
        String dateTimeValue = "20250305 10:20";
        StringTypeDto dto = new StringTypeDto(dateValue, dateTimeValue);
        // when
        DateTypeDto actual = client
                .post()
                .uri("/deserialize")
                .body(dto)
                .retrieve()
                .body(DateTypeDto.class);
        // then
        DateTypeDto expected = new DateTypeDto(
                LocalDate.parse(dateValue, dateFormatter),
                LocalDateTime.parse(dateTimeValue, dateTimeFormatter));
        assertThat(actual).isEqualTo(expected);
    }
}