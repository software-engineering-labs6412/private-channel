package org.ssau.privatechannel.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.utils.SystemContext;

@Configuration
public class ServletConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        String appPort = SystemContext.getProperty(SystemProperties.APP_PORT);
        factory.setPort(Integer.parseInt(appPort));
    }
}