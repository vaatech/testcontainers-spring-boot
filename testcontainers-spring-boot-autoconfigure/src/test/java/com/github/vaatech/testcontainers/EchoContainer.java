package com.github.vaatech.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class EchoContainer extends GenericContainer<EchoContainer> {
    public EchoContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }
}
