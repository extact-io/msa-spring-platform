package io.extact.msa.spring.platform.fw.external.jwt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * レスポンスヘッダからJsonWebTokenを取得し通知を行うクラス
 */
public class CustomizableClientRequest implements ClientHttpRequest {

    private ClientHttpRequest delegate;
    private ApplicationEventPublisher publisher;

    public CustomizableClientRequest(ClientHttpRequest delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public ClientHttpResponse execute() throws IOException {
        ClientHttpResponse response = delegate.execute();
        return handleResponse(response);
    }

    @Override
    public HttpMethod getMethod() {
        return delegate.getMethod();
    }

    @Override
    public URI getURI() {
        return delegate.getURI();
    }

    @Override
    public HttpHeaders getHeaders() {
        return delegate.getHeaders();
    }

    @Override
    public OutputStream getBody() throws IOException {
        return delegate.getBody();
    }

    private ClientHttpResponse handleResponse(ClientHttpResponse response) {

        if (!response.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return response;
        }

        String bearerToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        publisher.publishEvent(new JwtPublishedEvent(Pair.of(HttpHeaders.AUTHORIZATION, bearerToken)));

        return response;
    }
}
