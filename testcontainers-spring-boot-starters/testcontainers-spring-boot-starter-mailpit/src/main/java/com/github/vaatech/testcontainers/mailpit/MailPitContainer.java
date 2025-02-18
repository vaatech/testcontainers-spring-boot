package com.github.vaatech.testcontainers.mailpit;

import lombok.NonNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MailPitContainer extends GenericContainer<MailPitContainer> {

    public MailPitContainer(@NonNull final DockerImageName dockerImageName) {
        super(dockerImageName);
    }
}
