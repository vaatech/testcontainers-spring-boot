package com.github.vaatech.test.docker.common.spring;

import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;

@Slf4j
public abstract class AbstractDockerContainer<SELF extends GenericContainer<SELF>>
    extends GenericContainer<SELF> implements DockerContainer {

  protected AbstractDockerContainer(String dockerImageName) {
    super(dockerImageName);
  }

  protected Runnable afterStartCallback = () -> {};

  @Override
  protected void containerIsStarted(InspectContainerResponse containerInfo) {
    super.containerIsStarted(containerInfo);
    afterStartCallback.run();
  }

}
