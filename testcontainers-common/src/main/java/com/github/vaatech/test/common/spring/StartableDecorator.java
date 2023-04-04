package com.github.vaatech.test.common.spring;

import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

@Slf4j
public abstract class StartableDecorator implements DockerContainer {

  private final GenericContainer<?> container;
  private ConfigurableEnvironment environment;

  protected StartableDecorator(GenericContainer<?> container) {
    this.container = container;
  }

  @Override
  public void setEnvironment(ConfigurableEnvironment environment) {
    this.environment = environment;
  }

  @Override
  public Logger log() {
    return log;
  }

  protected abstract void registerContainerToEnvironment(
      ConfigurableEnvironment environment, InspectContainerResponse containerInfo);

  @Override
  public String name() {
    return container.getDockerImageName();
  }

  @Override
  public List<Integer> getExposedPorts() {
    return container.getExposedPorts();
  }

  @Override
  public InspectContainerResponse getContainerInfo() {
    return container.getContainerInfo();
  }

  @Override
  public void start() {
    container.start();
    registerContainerToEnvironment(environment, container.getContainerInfo());
  }

  @Override
  public void stop() {
    container.stop();
  }

  @Override
  public GenericContainer<?> unwrap() {
    return container;
  }
}
