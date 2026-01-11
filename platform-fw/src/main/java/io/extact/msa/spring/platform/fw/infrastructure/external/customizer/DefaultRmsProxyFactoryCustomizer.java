package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.Optional;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRmsProxyFactoryCustomizer implements RmsProxyFactoryCustomizer {

    private final RestClient restClient;
    private final Optional<ConversionService> optional;

    @Override
    public void customize(HttpServiceProxyFactory.Builder factoryBuilder) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        factoryBuilder.exchangeAdapter(adapter);
        optional.ifPresent(factoryBuilder::conversionService);
    }
}
