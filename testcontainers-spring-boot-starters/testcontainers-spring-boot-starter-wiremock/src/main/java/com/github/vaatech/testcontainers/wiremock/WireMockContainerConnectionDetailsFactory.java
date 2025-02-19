package com.github.vaatech.testcontainers.wiremock;

import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;
import org.wiremock.integrations.testcontainers.WireMockContainer;

class WireMockContainerConnectionDetailsFactory
        extends ContainerConnectionDetailsFactory<WireMockContainer, WireMockConnectionDetails> {
    @Override
    protected WireMockConnectionDetails
    getContainerConnectionDetails(final ContainerConnectionSource<WireMockContainer> source) {
        return new WireMockContainerConnectionDetails(source);
    }

    private static final class WireMockContainerConnectionDetails
            extends ContainerConnectionDetails<WireMockContainer>
            implements WireMockConnectionDetails {

        private WireMockContainerConnectionDetails(
                ContainerConnectionSource<WireMockContainer> source) {
            super(source);
        }

        @Override
        public String url() {
            return getContainer().getBaseUrl();
        }

        @Override
        public String host() {
            return getContainer().getHost();
        }

        @Override
        public int port() {
            return getContainer().getPort();
        }
    }
}