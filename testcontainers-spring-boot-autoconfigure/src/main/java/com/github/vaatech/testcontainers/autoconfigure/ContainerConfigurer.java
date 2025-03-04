package com.github.vaatech.testcontainers.autoconfigure;

import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import org.testcontainers.containers.GenericContainer;

import java.util.stream.Stream;

public interface ContainerConfigurer {

    <C extends GenericContainer<?>> C configure(final C container,
                                                final CommonContainerProperties properties,
                                                final Stream<ContainerCustomizer<C>> customizers);

    <C extends GenericContainer<?>> C configure(final C container,
                                                final CommonContainerProperties properties);
}
