package io.extact.msa.spring.platform.fw.stub.apps.person.infrastructure.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.fw.domain.model.ModelValidator;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SingleRestClientConfig;
import io.extact.msa.spring.platform.fw.infrastructure.persistence.DefaultModelEntityMapper;

@Configuration(proxyBeanMethods = false)
@Import(SingleRestClientConfig.class)
public class RemotePersonRepositoryConfig {

    @Bean
    @ConditionalOnMissingBean // ExternalPersonClientConfigで先に登録されていた場合はskip
    @ConfigurationProperties("rms.persistence.person.remote")
    ExternalProperties externalProperties() {
        return new ExternalProperties();
    }

    @Bean
    RemotePersonClientApi remotePersonClientApi(HttpServiceProxyFactory factory) {
        return factory.createClient(RemotePersonClientApi.class);
    }

    @Bean
    RemotePersonRepository remotePersonRepository(RemotePersonClientApi client, ModelValidator validator) {
        return new RemotePersonRepository(
                client,
                new DefaultModelEntityMapper<>(RemotePerson::from, validator));
    }
}
