package com.github.vaatech.testcontainers.autoconfigure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DockerPresenceMarkerTest {

    @Test
    void markerShouldBlockContextIfDockerIsAbsent() {
        assertThatThrownBy(() -> new DockerPresenceMarker(false))
                .isInstanceOf(DockerNotPresentException.class);
    }
}