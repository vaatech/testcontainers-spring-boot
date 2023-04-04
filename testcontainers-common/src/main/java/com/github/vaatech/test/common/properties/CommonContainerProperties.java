package com.github.vaatech.test.common.properties;

import lombok.Data;
import org.testcontainers.utility.DockerImageName;

@Data
public abstract class CommonContainerProperties {

  private DockerImage dockerImage;

  public abstract DockerImage getDefaultDockerImage();

  public static class DockerImage {

    private final DockerImageName dockerImageName;

    private final String registry;
    private final String name;
    private final String version;

    public DockerImage(String name) {
      var dockerImageName = DockerImageName.parse(name);

      this.dockerImageName = dockerImageName;
      this.registry = dockerImageName.getRegistry();
      this.name = dockerImageName.getUnversionedPart();
      this.version = dockerImageName.getVersionPart();
    }

    public String getRegistry() {
      return registry;
    }

    public String getName() {
      return name;
    }

    public String getVersion() {
      return version;
    }

    public String fullImageName() {
      return dockerImageName.asCanonicalNameString();
    }

    public static DockerImage create(String name) {
      return new DockerImage(name);
    }
  }
}
