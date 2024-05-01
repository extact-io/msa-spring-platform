package io.extact.msa.spring.platform.core.health.client;

public interface ReadinessCheckRestClientFactory {
    ReadinessCheckRestClient create(String baseUrl);
}
