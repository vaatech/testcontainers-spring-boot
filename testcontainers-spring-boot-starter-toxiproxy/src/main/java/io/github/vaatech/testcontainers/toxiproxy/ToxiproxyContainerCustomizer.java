package io.github.vaatech.testcontainers.toxiproxy;

import io.github.vaatech.testcontainers.ContainerCustomizer;
import org.testcontainers.containers.ToxiproxyContainer;

@FunctionalInterface
public interface ToxiproxyContainerCustomizer extends ContainerCustomizer<ToxiproxyContainer> {
}
