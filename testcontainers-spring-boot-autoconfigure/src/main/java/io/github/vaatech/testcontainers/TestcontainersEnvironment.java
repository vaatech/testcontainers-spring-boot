package io.github.vaatech.testcontainers;

import io.github.vaatech.testcontainers.util.ContainerUtils;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.function.Consumer;

public record TestcontainersEnvironment(GenericContainer<?>[] containers,
                                        Network network,
                                        Consumer<ContainerState[]> callback) {

    public void run() {
        ContainerUtils.run(containers);
        callback.accept(containers);
    }

}
