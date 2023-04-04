package com.github.vaatech.test.database.postgresql;

import com.github.vaatech.test.common.spring.ContainerCustomizer;
import org.testcontainers.containers.PostgreSQLContainer;

public interface PostgreSQLContainerCustomizer extends ContainerCustomizer<PostgreSQLContainer<?>> {}
