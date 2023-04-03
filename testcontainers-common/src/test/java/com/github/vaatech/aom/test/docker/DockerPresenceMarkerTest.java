package com.github.vaatech.aom.test.docker;

import com.github.vaatech.test.docker.common.spring.DockerNotPresentException;
import com.github.vaatech.test.docker.common.spring.DockerPresenceMarker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DockerPresenceMarkerTest {

  @Test
  void markerShouldBlockContextIfDockerIsAbsent() {
    assertThatThrownBy(() -> new DockerPresenceMarker(false))
        .isInstanceOf(DockerNotPresentException.class);
  }
}
