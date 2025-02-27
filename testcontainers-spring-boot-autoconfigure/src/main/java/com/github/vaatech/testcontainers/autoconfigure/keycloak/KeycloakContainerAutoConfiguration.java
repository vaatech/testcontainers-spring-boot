package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.ContainerFactory;
import com.github.vaatech.testcontainers.autoconfigure.mailpit.ConditionalOnMailPitContainerEnabled;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistrar;

import static com.github.vaatech.testcontainers.autoconfigure.keycloak.KeycloakProperties.BEAN_NAME_CONTAINER_KEYCLOAK;

@AutoConfiguration(
        before = ServiceConnectionAutoConfiguration.class,
        after = DockerPresenceAutoConfiguration.class)
@ConditionalOnMailPitContainerEnabled
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakContainerAutoConfiguration {

    @Bean(name = BEAN_NAME_CONTAINER_KEYCLOAK, destroyMethod = "stop")
    KeycloakContainer
    keycloak(final KeycloakProperties properties,
             final ContainerCustomizers<KeycloakContainer, ContainerCustomizer<KeycloakContainer>> customizers) {

        final var typeParam = new ParameterizedTypeReference<KeycloakContainer>() {
        };
        KeycloakContainer keycloakContainer = ContainerFactory.createContainer(properties, KeycloakContainer.class);
        return customizers.customize(keycloakContainer);
    }

    @Bean
    ContainerCustomizers<KeycloakContainer, ContainerCustomizer<KeycloakContainer>>
    keycloakContainerCustomizers(final ObjectProvider<ContainerCustomizer<KeycloakContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }

    @Bean
    DynamicPropertyRegistrar keycloakContainerProperties(final KeycloakConnectionDetails connectionDetails) {
        return registry -> {
//            registry.add("container.wiremock.baseUrl", connectionDetails::url);
//            registry.add("container.wiremock.host", connectionDetails::host);
//            registry.add("container.wiremock.port", connectionDetails::port);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    Keycloak keycloakAdmin(KeycloakContainer container) {
        return container.getKeycloakAdminClient();
    }
}
