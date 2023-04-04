package com.github.vaatech.test.database.mysql;

import com.github.vaatech.test.common.spring.ContainerCustomizer;
import org.testcontainers.containers.MySQLContainer;

public interface MySQLContainerCustomizer extends ContainerCustomizer<MySQLContainer<?>> {}
