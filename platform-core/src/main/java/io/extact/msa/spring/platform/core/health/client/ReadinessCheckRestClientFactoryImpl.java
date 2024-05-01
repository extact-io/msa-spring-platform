package io.extact.msa.spring.platform.core.health.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import jakarta.enterprise.context.Dependent;

@Dependent
public class ReadinessCheckRestClientFactoryImpl implements ReadinessCheckRestClientFactory {

    @Override
    public ReadinessCheckRestClient create(String baseUrl) {
        try {
            return RestClientBuilder.newBuilder()
                    .baseUri(new URI(baseUrl))
                    .build(ReadinessCheckRestClient.class);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
