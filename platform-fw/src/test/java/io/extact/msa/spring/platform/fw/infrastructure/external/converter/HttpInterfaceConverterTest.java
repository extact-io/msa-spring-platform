package io.extact.msa.spring.platform.fw.infrastructure.external.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriBuilderFactory;

import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConverterClientApi.DateTypeDto;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConverterClientApi.StringTypeDto;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpInterfaceConverterTest {

    private static DateTimeFormatter dateFormatter;
    private static DateTimeFormatter dateTimeFormatter;

    @Autowired
    private ConverterClientApi converterClient;

    @Configuration(proxyBeanMethods = false)
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
        ConverterClientApi converterClientApi(Environment env) {

            /*
             * HttpInterfaceの型変換にはUriBuilderFactoryは使用されないため、
             * CustomeUriBuilderFactoryは使わなくてもOK
             */
            UriBuilderFactory uriFactory = new LocalHostUriBuilderFactory(env);

            HttpMessageConverter<Object> converter = ConfigMessageConveterBuilder
                    .builder(env)
                    .build();
            ConversionService conversionService = ConfigConversionServiceBuilder
                    .builder(env)
                    .build();

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(uriFactory)
                    .messageConverters(converters -> converters.addFirst(converter))
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory
                    .builderFor(adapter)
                    .conversionService(conversionService)
                    .build();

            return factory.createClient(ConverterClientApi.class);
        }
    }

    @BeforeAll
    static void beforeAll(
            @Value("${rms.rest.client.format.date}") String datePattern,
            @Value("${rms.rest.client.format.date-time}") String dateTimePattern) {
        dateFormatter = DateTimeFormatter.ofPattern(datePattern);
        dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
    }

    @Test
    void testPathLocalDate() {
        // given
        String value = "20250305";
        LocalDate date = LocalDate.parse(value, dateFormatter);
        // when
        String actual = converterClient.pathLocalDate(date);
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testPathLocalDateTime() {
        // given
        String value = "20250305 10:20";
        LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);
        // when
        String actual = converterClient.pathLocalDateTime(dateTime);
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testParamLocalDate() {
        // given
        String value = "20250305";
        LocalDate date = LocalDate.parse(value, dateFormatter);
        // when
        String actual = converterClient.paramLocalDate(date);
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testParamLocalDateTime() {
        // given
        String value = "20250305 10:20";
        LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);
        // when
        String actual = converterClient.paramLocalDateTime(dateTime);
        // then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    void testReturnLocalDate() {
        // given
        String value = "20250305";
        LocalDate expected = LocalDate.parse(value, dateFormatter);
        // when
        LocalDate actual = converterClient.returnLocalDate(value); // 単項目だがJSON deserializeされる
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testReturnLocalDateTime() {
        // given
        String value = "20250305 10:20";
        LocalDateTime expected = LocalDateTime.parse(value, dateTimeFormatter);
        // when
        LocalDateTime actual = converterClient.returnLocalDateTime(value); // 単項目だがJSON deserializeされる
        // then
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
        StringTypeDto actual = converterClient.serializeDto(dto);
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
        DateTypeDto actual = converterClient.deserializeDto(dto);
        // then
        DateTypeDto expected = new DateTypeDto(
                LocalDate.parse(dateValue, dateFormatter),
                LocalDateTime.parse(dateTimeValue, dateTimeFormatter));
        assertThat(actual).isEqualTo(expected);
    }
}