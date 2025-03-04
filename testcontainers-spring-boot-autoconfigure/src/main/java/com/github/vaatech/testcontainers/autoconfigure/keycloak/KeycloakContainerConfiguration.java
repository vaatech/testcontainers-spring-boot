package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import com.github.vaatech.testcontainers.autoconfigure.ContainerConfigurer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.keycloak.KeycloakProperties;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import static com.github.vaatech.testcontainers.keycloak.KeycloakProperties.BEAN_NAME_CONTAINER_KEYCLOAK;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KeycloakContainer.class)
@ConditionalOnMissingBean(KeycloakContainer.class)
@ConditionalOnKeycloakContainerEnabled
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakContainerConfiguration {

    public static final String KEYCLOAK_NETWORK_ALIAS = "keycloak.testcontainer.docker";

    @ServiceConnection(type = KeycloakConnectionDetails.class)
    @Bean(name = BEAN_NAME_CONTAINER_KEYCLOAK, destroyMethod = "stop")
    KeycloakContainer
    keycloak(final KeycloakProperties properties,
             final ContainerFactory containerFactory,
             final ContainerConfigurer configurer,
             final ObjectProvider<ContainerCustomizer<KeycloakContainer>> customizers) {

        KeycloakContainer keycloakContainer = containerFactory.createContainer(properties, KeycloakContainer.class, String.class);
        return configurer.configure(keycloakContainer, properties, customizers.orderedStream());
    }

    @Bean
    @Order(0)
    ContainerCustomizer<KeycloakContainer> keycloakContainerCustomizer(final KeycloakProperties properties) {
        return keycloak -> {
            keycloak.withEnv("KC_HTTP_ENABLED", "true");
            keycloak.withContextPath(properties.getContextPath());
            keycloak.withAdminUsername(properties.getAdminUser());
            keycloak.withAdminPassword(properties.getAdminPassword());
            keycloak.withNetworkAliases(KEYCLOAK_NETWORK_ALIAS);
        };
    }
}
