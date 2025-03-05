package com.github.vaatech.testcontainers.examples.keycloak.test.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.CLIENT_UUID;
import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.REALM;

@Configuration(proxyBeanMethods = false)
public class ClientRedirectUriConfiguration {

    @Bean
    ApplicationListener<ServletWebServerInitializedEvent> ready(final Keycloak keycloak) {
        return event -> {
            int port = event.getWebServer().getPort();
            ClientResource clientResource = keycloak.realm(REALM).clients().get(CLIENT_UUID);
            ClientRepresentation representation = clientResource.toRepresentation();
            representation.setRedirectUris(List.of("http://localhost:%s/login/oauth2/code/test".formatted(port)));
            clientResource.update(representation);
        };
    }
}
