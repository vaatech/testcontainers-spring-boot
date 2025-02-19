package com.github.vaatech.testcontainers.mailpit;

import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

public class MailPitContainerConnectionDetailsFactory
        extends ContainerConnectionDetailsFactory<MailPitContainer, MailPitConnectionDetails> {

    @Override
    protected MailPitConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<MailPitContainer> source) {
        return new WireMockContainerConnectionDetails(source);
    }

    private static final class WireMockContainerConnectionDetails
            extends ContainerConnectionDetails<MailPitContainer>
            implements MailPitConnectionDetails {

        private WireMockContainerConnectionDetails(
                ContainerConnectionSource<MailPitContainer> source) {
            super(source);
        }

        @Override
        public String host() {
            return getContainer().getHost();
        }

        @Override
        public int portHttp() {
            return getContainer().getHttpPort();
        }

        @Override
        public int portSMTP() {
            return getContainer().getSmtpPort();
        }

        @Override
        public String serverUrl() {
            return getContainer().getServerUrl();
        }
    }
}
