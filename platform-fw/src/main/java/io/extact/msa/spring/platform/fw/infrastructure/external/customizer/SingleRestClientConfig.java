package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriBuilderFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.CustomUriBuilderFactory;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigConversionServiceBuilder;

/**
 * 接続先が1つしかない場合に利用するRestClientのデフォルト設定。
 * 接続先が複数ある場合は接続先ごとにQualifireでCustomizerの設定が必要となるため、
 * このコンフィグは使えない。
 */
@Configuration(proxyBeanMethods = false)
public class SingleRestClientConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RmsRestClientObjectMapperCustomizer defaultItemObjectMapperCustomizer(ExternalProperties props) {
        return new DefaultRmsRestClientObjectMapperCustomizer(props);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @Scope(SCOPE_PROTOTYPE) // 状態を持つのでprototypeにしておく
    RmsRestClientCustomizer defaultItemRestClientCustomizer(
            ExternalProperties props,
            List<RmsRestClientObjectMapperCustomizer> mapperCustomizers) {
        return new DefaultRmsRestClientCustomizer(props, mapperCustomizers);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RmsProxyFactoryCustomizer defaultItemProxyFactoryCustomizer(
            ApplicationContext context,
            RestClient.Builder builder, // RestClientAutoConfigurationでCustomierが提供済みのBuilderを使用する
            List<RmsRestClientCustomizer> customizers) {
        RmsRestClientFactory factory = new RmsRestClientFactory(builder, context);
        return new DefaultRmsProxyFactoryCustomizer(factory.create(customizers), factory.appliedConversionService());
    }

    @Bean
    HttpServiceProxyFactory defaultHttpServiceProxyFactory(List<RmsProxyFactoryCustomizer> customizers) {
        HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builder();
        customizers.forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }

    // 使うことはなにをやっているか一目でわかるようにデフォルトのCustomizerをすべて展開した処理を書いておく
    static <T> T applyExpandedDefaultCustomizers(ExternalProperties props, ApplicationContext context, Class<T> clazz) {

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .applicationContext(context);

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

        ObjectMapper mapper = builder.createXmlMapper(false).build();
        HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter(mapper);

        ConversionService conversionService = ConfigConversionServiceBuilder
                .builder(props)
                .build();
        UriBuilderFactory uriFactory = CustomUriBuilderFactory.newInstance()
                .env(context.getEnvironment())
                .conversionService(conversionService)
                .uriTemplate(props.getUrl())
                .build();

        RestClient restClient = RestClient.builder()
                .uriBuilderFactory(uriFactory)
                .messageConverters(converters -> converters.addFirst(converter))
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer(mapper)))
                .requestInitializer(new LoginUserHeaderRequestInitializer())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .conversionService(conversionService)
                .build();

        return factory.createClient(clazz);
    }
}
