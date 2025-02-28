package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistrar;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(WireMockProperties.class)
@Import(WireMockContainerConfiguration.class)
public class WireMockConnectionAutoConfiguration {

    @Bean
    DynamicPropertyRegistrar wireMockContainerProperties(final WireMockConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.wiremock.baseUrl", connectionDetails::url);
            registry.add("container.wiremock.host", connectionDetails::host);
            registry.add("container.wiremock.port", connectionDetails::port);
        };
    }

    @Bean
    @ConditionalOnClass(WireMock.class)
    @ConditionalOnMissingBean(WireMock.class)
    WireMock wireMock(final WireMockConnectionDetails connectionDetails) {
        WireMock wireMock = new WireMock(connectionDetails.host(), connectionDetails.port());
        WireMock.configureFor(wireMock);
        return wireMock;
    }
}
