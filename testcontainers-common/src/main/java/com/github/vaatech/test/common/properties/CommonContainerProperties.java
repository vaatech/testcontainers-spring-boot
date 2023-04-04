package com.github.vaatech.test.common.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class CommonContainerProperties {
  private DockerImage dockerImage;

  public abstract DockerImage getDefaultDockerImage();

  public record DockerImage(String registry, String name, String version) {

    public DockerImage {
      if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Name cannot be blank.");
      }
      if (version != null && version.isBlank()) {
        throw new IllegalArgumentException("Version cannot be blank.");
      }
    }

    public String fullImageName() {
      if (registry == null || registry.isBlank()) return String.format("%s:%s", name, version);
      return String.format("%s/%s:%s", registry, name, version);
    }
  }
}
