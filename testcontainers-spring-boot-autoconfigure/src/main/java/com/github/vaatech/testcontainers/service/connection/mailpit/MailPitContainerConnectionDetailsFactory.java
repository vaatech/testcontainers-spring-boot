package com.github.vaatech.testcontainers.service.connection.mailpit;

import com.github.vaatech.testcontainers.autoconfigure.mailpit.MailPitConnectionDetails;
import com.github.vaatech.testcontainers.mailpit.MailPitContainer;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

public class MailPitContainerConnectionDetailsFactory
        extends ContainerConnectionDetailsFactory<MailPitContainer, MailPitConnectionDetails> {

    @Override
    protected MailPitConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<MailPitContainer> source) {
        return new MailPitContainerConnectionDetails(source);
    }

    private static final class MailPitContainerConnectionDetails
            extends ContainerConnectionDetails<MailPitContainer>
            implements MailPitConnectionDetails {

        private MailPitContainerConnectionDetails(
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
