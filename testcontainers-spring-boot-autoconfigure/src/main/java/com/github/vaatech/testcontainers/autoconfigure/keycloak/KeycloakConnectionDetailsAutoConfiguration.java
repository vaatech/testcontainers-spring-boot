package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeycloakConnectionDetails.class)
    KeycloakConnectionDetails keycloakConnectionDetails(KeycloakProperties properties) {
        return new PropertiesKeycloakConnectionDetails(properties);
    }
}
