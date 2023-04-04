package com.github.vaatech.test.database.postgresql;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.vaatech.test.common.spring.StartableDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.LinkedHashMap;

@Slf4j
public class PostgreSQLContainerDecorator extends StartableDecorator {

  private final PostgreSQLContainer<?> postgresql;

  protected PostgreSQLContainerDecorator(PostgreSQLContainer<?> container) {
    super(container);
    this.postgresql = container;
  }

  @Override
  protected void registerContainerToEnvironment(
      ConfigurableEnvironment environment, InspectContainerResponse containerInfo) {

    Integer mappedPort = postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
    String host = postgresql.getHost();

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put("container.postgresql.port", mappedPort);
    map.put("container.postgresql.host", host);
    map.put("container.postgresql.database", postgresql.getDatabaseName());
    map.put("container.postgresql.username", postgresql.getUsername());
    map.put("container.postgresql.password", postgresql.getPassword());

    String jdbcURL = "jdbc:postgresql://{}:{}/{}";
    log.info(
        "Started postgresql server. Connection details: {}, " + "JDBC connection url: " + jdbcURL,
        map,
        host,
        mappedPort,
        postgresql.getDatabaseName());

    MapPropertySource propertySource = new MapPropertySource("containerPostgreInfo", map);
    environment.getPropertySources().addFirst(propertySource);
  }
}
