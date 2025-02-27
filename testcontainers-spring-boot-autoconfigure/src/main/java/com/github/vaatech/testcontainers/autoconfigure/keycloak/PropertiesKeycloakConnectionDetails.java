package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import static java.lang.String.format;

public class PropertiesKeycloakConnectionDetails implements KeycloakConnectionDetails {

    private final KeycloakProperties properties;

    public PropertiesKeycloakConnectionDetails(KeycloakProperties properties) {
        this.properties = properties;
    }

    @Override
    public String host() {
        return properties.getHost();
    }

    @Override
    public Integer httpPort() {
        return properties.getHttpPort();
    }

    @Override
    public String authServerUrl() {
        return format("http://%s:%d%s", host(), httpPort(), properties.getContextPath());
    }
}
