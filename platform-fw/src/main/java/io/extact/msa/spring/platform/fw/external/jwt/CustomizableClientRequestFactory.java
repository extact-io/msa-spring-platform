package io.extact.msa.spring.platform.fw.external.jwt;

import java.io.IOException;
import java.net.URI;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

public class CustomizableClientRequestFactory implements ClientHttpRequestFactory {

    private ClientHttpRequestFactory delegate;
    private ApplicationEventPublisher publisher;

    public CustomizableClientRequestFactory(ClientHttpRequestFactory delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        ClientHttpRequest delegatedRequest = delegate.createRequest(uri, httpMethod);
        return new CustomizableClientRequest(delegatedRequest, publisher);
    }
}
