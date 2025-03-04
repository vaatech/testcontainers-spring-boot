package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.vaatech.testcontainers.wiremock.WireMockProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(WireMockProperties.class)
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WireMockConnectionDetails.class)
    WireMockConnectionDetails wireMockConnectionDetails(WireMockProperties properties) {
        return new PropertiesWireMockConnectionDetails(properties);
    }
}
