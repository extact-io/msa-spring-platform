package io.extact.msa.spring.platform.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration(proxyBeanMethods = false)
public class CoreConfiguration {

    @Bean
    @ConditionalOnWebApplication(type = Type.SERVLET)
    TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        LogbackValve valve = new LogbackValve();
        valve.setFilename(LogbackValve.DEFAULT_FILENAME);
        tomcatServletWebServerFactory.addContextValves(valve);
        return tomcatServletWebServerFactory;
    }
}
