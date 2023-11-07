package io.github.vaatech.testcontainers.common.spring;

import org.testcontainers.containers.GenericContainer;

public class EchoContainer extends GenericContainer<EchoContainer> {
    public EchoContainer() {
        super("alpine:3.18.4");
    }
}
