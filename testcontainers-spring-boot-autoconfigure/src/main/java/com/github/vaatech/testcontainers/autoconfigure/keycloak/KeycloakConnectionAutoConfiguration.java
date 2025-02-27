package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistrar;

import static dasniko.testcontainers.keycloak.ExtendableKeycloakContainer.ADMIN_CLI_CLIENT;
import static dasniko.testcontainers.keycloak.ExtendableKeycloakContainer.MASTER_REALM;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(KeycloakProperties.class)
@Import(KeycloakContainerConfiguration.class)
public class KeycloakConnectionAutoConfiguration {

    @Bean
    DynamicPropertyRegistrar keycloakContainerProperties(final KeycloakConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.wiremock.host", connectionDetails::host);
            registry.add("container.wiremock.http-port", connectionDetails::httpPort);
            registry.add("container.wiremock.auth-server-url", connectionDetails::authServerUrl);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    Keycloak keycloakAdmin(final KeycloakConnectionDetails connectionDetails,
                           final KeycloakProperties properties) {
        return Keycloak.getInstance(
                connectionDetails.authServerUrl(),
                MASTER_REALM,
                properties.getAdminUser(),
                properties.getAdminPassword(),
                ADMIN_CLI_CLIENT);
    }
}
