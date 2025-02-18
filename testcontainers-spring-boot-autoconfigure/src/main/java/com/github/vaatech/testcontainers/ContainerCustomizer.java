package com.github.vaatech.testcontainers;

import org.testcontainers.containers.GenericContainer;

@FunctionalInterface
public interface ContainerCustomizer<T extends GenericContainer<?>> {

    void customize(T container);
}
