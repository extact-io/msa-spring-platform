package io.extact.msa.spring.platform.core.health.client;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import io.extact.msa.spring.platform.core.async.AsyncInvoker;


public class ReadinessProbeRestClientFactoryImpl implements ReadinessProbeRestClientFactory {

    private AsyncInvoker asyncInvoker;

    public ReadinessProbeRestClientFactoryImpl(AsyncInvoker asyncInvoker) {
        this.asyncInvoker = asyncInvoker;
    }

    @Override
    public ReadinessProbeRestClient create() {
        return new ReadinessProbeRestClientImpl(asyncInvoker, createRestClient(),
                StatusHttpCodeMapper.defaultMapping());
    }

    private RestClient createRestClient() {
        return RestClient.builder()
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
        public void handleError(ClientHttpResponse response) throws IOException {
            // nop
        }
    }
}
