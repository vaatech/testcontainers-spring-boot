package com.github.vaatech.testcontainers.mailpit;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MailPitContainer extends GenericContainer<MailPitContainer> {

    public static final Integer HTTP_PORT = 8025;

    public static final Integer SMTP_PORT = 1025;

    public MailPitContainer(@NonNull final DockerImageName dockerImageName) {
        super(dockerImageName);

        addExposedPorts(HTTP_PORT, SMTP_PORT);
    }

    public String getServerUrl() {
        return String.format("http://%s:%d", getHost(), getHttpPort());
    }

    public Integer getHttpPort() {
        return getMappedPort(HTTP_PORT);
    }

    public Integer getSmtpPort() {
        return getMappedPort(SMTP_PORT);
    }
}
