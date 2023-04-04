package com.github.vaatech.test.common.spring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DockerPresenceMarkerTest {

  @Test
  void markerShouldBlockContextIfDockerIsAbsent() {
    assertThatThrownBy(() -> new DockerPresenceMarker(false))
        .isInstanceOf(DockerNotPresentException.class);
  }
}
