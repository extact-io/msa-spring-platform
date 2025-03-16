package io.extact.msa.spring.platform.fw.stub.client.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

@Configuration(proxyBeanMethods = false)
public class ConverterClientConfig {

    @Bean
    ConverterClientApi clientApi(Environment env) {

        RestClient restClient = RestClient.builder()
                .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ConverterClientApi.class);
    }
}
