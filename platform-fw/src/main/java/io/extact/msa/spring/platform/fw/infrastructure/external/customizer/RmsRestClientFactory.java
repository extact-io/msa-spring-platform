package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

public class RmsRestClientFactory {

    private DefaultRmsRestClientCustomizerContext context;
    private RestClient.Builder builder;

    public RmsRestClientFactory(RestClient.Builder builder, ApplicationContext applicationContext) {
        this.context = new DefaultRmsRestClientCustomizerContext(applicationContext);
        this.builder = builder;
    }

    public RestClient create(List<RmsRestClientCustomizer> customizers) {

        Supplier<Optional<ConversionService>> finder = () -> this.findLastAppiedConversionService(customizers);
        context.setFinder(finder);

        customizers.forEach(customizer -> customizer.customize(builder, context));
        return builder.build();
    }

    public Optional<ConversionService> appliedConversionService() {
        if (context.getFinder() == null) {
            throw new IllegalStateException("create method has not yet been called");
        }
        return context.currentAppiedConversionService();
    }

    // UriBuilderFactoryに最後に適用されたコンバーターが有効になっているので
    // HttpInterfaceにも同じものを適用する
    private Optional<ConversionService> findLastAppiedConversionService(List<RmsRestClientCustomizer> customizers) {
        return customizers
                .reversed()
                .stream()
                .map(RmsRestClientCustomizer::appliedConversionService)
                .filter(service -> service != null)
                .findFirst();
    }

    @RequiredArgsConstructor
    static class DefaultRmsRestClientCustomizerContext implements RmsRestClientCustomizerContext {

        private final ApplicationContext context;
        private Supplier<Optional<ConversionService>> finder;

        @Override
        public ApplicationContext getApplicationContext() {
            return context;
        }

        @Override
        public Optional<ConversionService> currentAppiedConversionService() {
            return finder.get();
        }

        Supplier<Optional<ConversionService>> getFinder() {
            return finder;
        }

        void setFinder(Supplier<Optional<ConversionService>> finder) {
            this.finder = finder;
        }
    }
}
