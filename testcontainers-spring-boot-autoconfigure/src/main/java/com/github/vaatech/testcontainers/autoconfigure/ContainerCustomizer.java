package com.github.vaatech.testcontainers.autoconfigure;

import org.testcontainers.containers.GenericContainer;

@FunctionalInterface
public interface ContainerCustomizer<T extends GenericContainer<?>> {

    void customize(T container);
}
