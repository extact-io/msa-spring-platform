package io.extact.msa.spring.platform.core.health.client;

public interface ReadnessCheckRestClientFactory {
    ReadnessCheckRestClient create(String baseUrl);
}
