package io.github.vaatech.testcontainers.mysql;

import io.github.vaatech.testcontainers.ContainerCustomizer;
import org.testcontainers.containers.MySQLContainer;

@FunctionalInterface
public interface MySQLContainerCustomizer extends ContainerCustomizer<MySQLContainer<?>> {}
