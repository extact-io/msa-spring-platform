package io.extact.msa.spring.platform.fw.stub.client.person.infrastructure;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.fw.LocalHostUriDefaultFormatExternalPropeties;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.customizer.SingleRestClientConfig;
import io.extact.msa.spring.platform.fw.stub.client.person.domain.ExternalPersonClient;

@Configuration(proxyBeanMethods = false)
public class ExternalPersonClientConfig {

    // SingleRestClientConfigはテスト対象のapps.person側で使ってるため
    // テストドライバとなるClientのHttpInterfaceはすべて手動で生成する
    @Bean
    ExternalPersonClient personClient(ApplicationContext context) {
        ExternalProperties props = new LocalHostUriDefaultFormatExternalPropeties(context.getEnvironment());
        ExternalPersonClientApi clientApi = SingleRestClientConfig.applyExpandedDefaultCustomizers(
                props,
                context,
                ExternalPersonClientApi.class);
        return new ExternalPersonClientAdapter(clientApi);
    }
}
