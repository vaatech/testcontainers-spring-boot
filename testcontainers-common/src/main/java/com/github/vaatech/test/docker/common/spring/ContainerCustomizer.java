package com.github.vaatech.test.docker.common.spring;

import org.testcontainers.containers.GenericContainer;

@FunctionalInterface
public interface ContainerCustomizer<T extends GenericContainer<?>> {

  void customize(T container);
}
