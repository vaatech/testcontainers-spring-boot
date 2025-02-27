package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface KeycloakConnectionDetails extends ConnectionDetails {
    String host();
    Integer httpPort();
    String authServerUrl();
}
