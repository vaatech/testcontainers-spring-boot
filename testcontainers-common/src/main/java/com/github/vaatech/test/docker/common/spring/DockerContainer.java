package com.github.vaatech.test.docker.common.spring;

import org.slf4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.lifecycle.Startable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public interface DockerContainer extends ContainerState, Startable {

  String name();

  void setEnvironment(ConfigurableEnvironment environment);

  default void init() {}

  Logger log();

  default void logInfo() {
    logInfo(" ");
  }

  default void logInfo(String offset) {
    log().info("{}{}:", offset, name());
    logNetworkInfo(offset + "\t");
  }

  private void logNetworkInfo(String offset) {
    List<Integer> exposedPorts = new ArrayList<>(this.getExposedPorts());
    exposedPorts.sort(Comparator.naturalOrder());

    log().info("{}Host: {}", offset, this.getHost());
    if (!exposedPorts.isEmpty()) {
      log().info("{}Ports:", offset);
    }

    offset += "\t";
    for (Integer port : exposedPorts) {
      Integer mappedPort = this.getMappedPort(port);
      log().info("{}{} -> {}", offset, port, Objects.toString(mappedPort, "NONE"));
    }
  }
}
