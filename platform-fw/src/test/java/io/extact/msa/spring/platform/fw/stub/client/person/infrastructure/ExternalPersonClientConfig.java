package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonClient;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

@TestConfiguration(proxyBeanMethods = false)
public class ExternalPersonClientConfig {

    @Bean
    ExternalPersonClient personClient(Environment env) {

        RestClient restClient = RestClient.builder()
                .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        ExternalPersonClientApi personApi = factory.createClient(ExternalPersonClientApi.class);

        return new ExternalPersonClientAdapter(personApi);
    }
}
