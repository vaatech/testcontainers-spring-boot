package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

public class KeycloakContainerConnectionDetailsFactory
        extends ContainerConnectionDetailsFactory<KeycloakContainer, KeycloakConnectionDetails> {

    @Override
    protected KeycloakConnectionDetails getContainerConnectionDetails(
            ContainerConnectionSource<KeycloakContainer> source) {
        return new KeycloakContainerConnectionDetails(source);
    }

    private static final class KeycloakContainerConnectionDetails
            extends ContainerConnectionDetails<KeycloakContainer>
            implements KeycloakConnectionDetails {

        private KeycloakContainerConnectionDetails(
                ContainerConnectionSource<KeycloakContainer> source) {
            super(source);
        }

        @Override
        public String host() {
            return getContainer().getHost();
        }

        @Override
        public Integer httpPort() {
            return getContainer().getHttpPort();
        }

        @Override
        public String authServerUrl() {
            return getContainer().getAuthServerUrl();
        }
    }
}
