package com.github.vaatech.testcontainers.wiremock;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = ServiceConnectionAutoConfiguration.class)
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WireMockConnectionDetails.class)
    WireMockConnectionDetails wireMockConnectionDetails(WireMockProperties properties) {
        return new WireMockConnectionDetails() {

            @Override
            public String url() {
                return properties.getBaseUrl();
            }

            @Override
            public String host() {
                return properties.getHost();
            }

            @Override
            public int port() {
                return properties.getPort();
            }
        };
    }
}
