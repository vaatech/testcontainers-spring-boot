package com.github.vaatech.test.database.mysql;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.vaatech.test.common.spring.DockerContainer;
import com.github.vaatech.test.common.spring.StartableDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.util.LinkedHashMap;

@Slf4j
public class MySQLContainerDecorator extends StartableDecorator {
  private final MySQLContainer<?> container;

  protected MySQLContainerDecorator(MySQLContainer<?> container) {
    super(container);
    this.container = container;
  }

  @Override
  protected void registerContainerToEnvironment(
      ConfigurableEnvironment environment, InspectContainerResponse containerInfo) {

    if (environment == null) {
      log.debug("Environment is not provided. Skipping registration.");
      return;
    }

    Integer mappedPort = getMappedPort(MySQLContainer.MYSQL_PORT);
    String host = container.getHost();
    String jdbcURL = container.getJdbcUrl();

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put("container.mysql.port", mappedPort);
    map.put("container.mysql.host", host);
    map.put("container.mysql.database", container.getDatabaseName());
    map.put("container.mysql.username", container.getUsername());
    map.put("container.mysql.password", container.getPassword());

    log.info("Started mysql server. Connection details: %s, JDBC connection url: " + jdbcURL, map);
    MapPropertySource propertySource = new MapPropertySource("containerMySQLInfo", map);
    environment.getPropertySources().addFirst(propertySource);
  }
}
