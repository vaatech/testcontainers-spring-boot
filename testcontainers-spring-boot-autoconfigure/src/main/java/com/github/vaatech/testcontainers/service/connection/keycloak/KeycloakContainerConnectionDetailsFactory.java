package com.github.vaatech.testcontainers.service.connection.keycloak;

import com.github.vaatech.testcontainers.autoconfigure.keycloak.KeycloakConnectionDetails;
import com.github.vaatech.testcontainers.keycloak.KeycloakContainer;
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
