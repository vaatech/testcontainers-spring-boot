package com.github.vaatech.testcontainers.examples.keycloak.test.config;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.keycloak.ConditionalOnKeycloakContainerEnabled;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration(proxyBeanMethods = false)
public class ImportFileConfiguration {

    @Bean
    @Order(2)
    @ConditionalOnKeycloakContainerEnabled
    ContainerCustomizer<KeycloakContainer>
    keycloakContainerImportRealmContainerCustomizer() {
        return container -> container.withRealmImportFile("/test-realm.json");
    }
}