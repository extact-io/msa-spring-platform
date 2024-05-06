package io.extact.msa.spring.platform.core.health.client;

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
        return RestClient.builder().build();
    }
}
