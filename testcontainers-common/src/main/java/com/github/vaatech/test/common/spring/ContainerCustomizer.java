package com.github.vaatech.test.common.spring;

import org.testcontainers.containers.GenericContainer;

@FunctionalInterface
public interface ContainerCustomizer<T extends GenericContainer<?>> {

  void customize(T container);
}
