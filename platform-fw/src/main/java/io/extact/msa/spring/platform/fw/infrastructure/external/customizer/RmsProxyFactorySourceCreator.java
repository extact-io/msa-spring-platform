package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.List;
import java.util.Optional;

import org.springframework.core.convert.ConversionService;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RmsProxyFactorySourceCreator {

    private final RestClient.Builder clientBuilder;
    private final List<RmsRestClientCustomizer> clientCustomizers;

    public Source create() {
        RmsRestClientCustomizerContext context = this::findLastAppiedConversionService;
        clientCustomizers.forEach(customizer -> customizer.customize(clientBuilder, context));
        RestClient restClient = clientBuilder.build();
        return new Source(restClient, context.currentAppiedConversionService());
    }

    // UriBuilderFactoryに最後に適用されたコンバーターが有効になっているので
    // HttpInterfaceにも同じものを適用する
    private Optional<ConversionService> findLastAppiedConversionService() {
        return clientCustomizers
                .reversed()
                .stream()
                .map(RmsRestClientCustomizer::appliedConversionService)
                .filter(service -> service != null)
                .findFirst();
    }

    public static record Source(
            RestClient restClient,
            Optional<ConversionService> conversionService) {
    }
}
