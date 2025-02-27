package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WireMockConnectionDetails.class)
    WireMockConnectionDetails wireMockConnectionDetails(WireMockProperties properties) {
        return new PropertiesWireMockConnectionDetails(properties);
    }
}
