package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SingleRestClientConfig;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonClient;

@Configuration(proxyBeanMethods = false)
@Import(SingleRestClientConfig.class)
public class ExternalPersonClientConfig {

    @Bean
    @ConditionalOnMissingBean // RemotePersonRepositoryConfigで先に登録されていた場合はskip
    @ConfigurationProperties("rms.persistence.person.remote")
    ExternalProperties externalProperties() {
        return new ExternalProperties();
    }

    @Bean
    ExternalPersonClient personClient(HttpServiceProxyFactory factory) {
        ExternalPersonClientApi personApi = factory.createClient(ExternalPersonClientApi.class);
        return new ExternalPersonClientAdapter(personApi);
    }
}
