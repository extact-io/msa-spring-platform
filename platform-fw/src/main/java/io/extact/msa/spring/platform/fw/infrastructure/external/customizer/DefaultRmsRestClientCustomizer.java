package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.util.UriBuilderFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.CustomUriBuilderFactory;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigConversionServiceBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRmsRestClientCustomizer implements RmsRestClientCustomizer, ApplicationContextAware {

    private final ExternalProperties props;
    private final List<RmsRestClientObjectMapperCustomizer> mapperCustomizers;

    private ApplicationContext context;
    private ConversionService applyConversionService; // HttpServiceProxyFactoryで利用するため

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public ConversionService appliedConversionService() {
        return applyConversionService;
    }

    @Override
    public void customize(Builder builder, RmsRestClientCustomizerContext unused) {

        applyConversionService = ConfigConversionServiceBuilder
                .builder(props)
                .build();
        UriBuilderFactory uriFactory = CustomUriBuilderFactory.newInstance()
                .env(context.getEnvironment())
                .conversionService(applyConversionService)
                .uriTemplate(props.getUrl())
                .build();
        ObjectMapper mapper = buildObjectMapper();
        HttpMessageConverter<Object> converter = new MappingJackson2HttpMessageConverter(mapper);

        builder.uriBuilderFactory(uriFactory)
                .messageConverters(converters -> converters.addFirst(converter))
                .requestInitializer(new LoginUserHeaderRequestInitializer())
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer(mapper)));
    }

    private ObjectMapper buildObjectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.applicationContext(context);
        mapperCustomizers.forEach(customizer -> customizer.customize(builder));
        return builder.createXmlMapper(false).build();
    }
}
