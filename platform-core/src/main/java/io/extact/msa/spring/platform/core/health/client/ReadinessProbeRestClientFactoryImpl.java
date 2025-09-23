package io.extact.msa.spring.platform.core.health.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import io.extact.msa.spring.platform.core.async.AsyncInvoker;


public class ReadinessProbeRestClientFactoryImpl implements ReadinessProbeRestClientFactory {

    private final AsyncInvoker asyncInvoker;
    private final RestClient.Builder builder;

    public ReadinessProbeRestClientFactoryImpl(AsyncInvoker asyncInvoker, RestClient.Builder builder) {
        this.asyncInvoker = asyncInvoker;
        this.builder = builder;
    }

    @Override
    public ReadinessProbeRestClient create() {
        return new ReadinessProbeRestClientImpl(asyncInvoker, createRestClient(),
                StatusHttpCodeMapper.defaultMapping());
    }

    private RestClient createRestClient() {
        return this.builder
                .defaultStatusHandler(NopResponseErrorHandler.INSTANCE) // prevent error handling
                .build();
    }

    static class NopResponseErrorHandler implements ResponseErrorHandler {

        private static ResponseErrorHandler INSTANCE = new NopResponseErrorHandler();

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return true;
        }

        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            // nop
        }
    }
}
