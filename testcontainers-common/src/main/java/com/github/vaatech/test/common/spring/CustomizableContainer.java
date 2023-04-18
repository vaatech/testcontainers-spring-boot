package com.github.vaatech.test.common.spring;

import org.testcontainers.containers.GenericContainer;

import java.util.List;

public interface CustomizableContainer<T extends GenericContainer<?>> {
  void setCustomizers(List<ContainerCustomizer<T>> customizers);
}
