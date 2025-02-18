package com.github.vaatech.testcontainers.mysql;

import com.github.vaatech.testcontainers.ContainerCustomizer;
import org.testcontainers.containers.MySQLContainer;

@FunctionalInterface
public interface MySQLContainerCustomizer extends ContainerCustomizer<MySQLContainer<?>> {}
