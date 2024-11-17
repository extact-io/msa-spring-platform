package io.extact.msa.spring.platform.core.log;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import ch.qos.logback.access.tomcat.LogbackValve;

public class LogbackAccessConfigCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {

        LogbackValve valve = new LogbackValve();
        valve.setFilename(LogbackValve.DEFAULT_FILENAME);

        factory.addContextValves(valve);
    }
}
