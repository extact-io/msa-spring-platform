package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriBuilderFactory;

import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.external.CustomUriBuilderFactory;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigConversionServiceBuilder;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigMessageConveterBuilder;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.DefaultModelEntityMapper;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class)
public class RemotePersonRepositoryConfig {

    @Bean
    @ConfigurationProperties("rms.persistence.person.remote")
    ExternalProperties externalProperties() {
        return new ExternalProperties();
    }

    @Bean
    RemotePersonClientApi remotePersonClientApi(ExternalProperties prop, Environment env) {

        ConversionService conversionService = ConfigConversionServiceBuilder
                .builder(prop)
                .build();
        UriBuilderFactory uriFactory = CustomUriBuilderFactory.newInstance()
                .env(env)
                .conversionService(conversionService)
                .uriTemplate(prop.getUrl())
                .build();
        HttpMessageConverter<Object> converter = ConfigMessageConveterBuilder
                .builder(prop)
                .build();

        RestClient restClient = RestClient.builder()
                .uriBuilderFactory(uriFactory)
                .messageConverters(converters -> converters.addFirst(converter))
                .requestInitializer(new LoginUserHeaderRequestInitializer())
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .conversionService(conversionService)
                .build();

        return factory.createClient(RemotePersonClientApi.class);
    }

    @Bean
    RemotePersonRepository remotePersonRepository(RemotePersonClientApi client, ModelValidator validator) {
        return new RemotePersonRepository(
                client,
                new DefaultModelEntityMapper<>(RemotePerson::from, validator));
    }
}
