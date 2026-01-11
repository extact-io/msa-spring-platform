package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.feature.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.RmsProxyFactorySourceCreator.Source;

@Configuration(proxyBeanMethods = false)
@Import(ValidatorConfig.class) // TODO:これは必要か？あとこれをテストで使うようにする
public class SimpleRestClientCustomizerConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RmsRestClientObjectMapperCustomizer defaultItemObjectMapperCustomizer(ExternalProperties props) {
        return new DefaultRmsRestClientObjectMapperCustomizer(props);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RmsRestClientCustomizer defaultItemRestClientCustomizer(
            ExternalProperties props,
            List<RmsRestClientObjectMapperCustomizer> mapperCustomizers) {
        return new DefaultRmsRestClientCustomizer(props, mapperCustomizers);
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    RmsProxyFactoryCustomizer defaultItemProxyFactoryCustomizer(
            RestClient.Builder builder, // RestClientAutoConfigurationでCustomierが提供済みのBuilderを使用する
            List<RmsRestClientCustomizer> customizers) {
        RmsProxyFactorySourceCreator creator = new RmsProxyFactorySourceCreator(builder, customizers);
        Source source = creator.create();
        return new DefaultRmsProxyFactoryCustomizer(source.restClient(), source.conversionService());
    }

    @Bean
    HttpServiceProxyFactory defaultHttpServiceProxyFactory(List<RmsProxyFactoryCustomizer> customizers) {
        HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builder();
        customizers.forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }
}
