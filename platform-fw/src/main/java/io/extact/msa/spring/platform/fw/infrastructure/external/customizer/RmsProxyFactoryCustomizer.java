package io.extact.msa.spring.platform.fw.infrastructure.external.customizer;

import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@FunctionalInterface
public interface RmsProxyFactoryCustomizer {

    void customize(HttpServiceProxyFactory.Builder builder);
}
