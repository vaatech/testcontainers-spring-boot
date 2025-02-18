package com.github.vaatech.testcontainers.util;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerState;

import java.util.Arrays;

@Slf4j
public class ContainerUtils {


    public static Container.ExecResult executeInContainer(ContainerState container, String... command) {
        try {
            return container.execInContainer(command);
        } catch (Exception e) {

            String format = String.format(
                    "Exception was thrown when executing: %s, for container: %s ",
                    Arrays.toString(command),
                    container.getContainerId()
            );

            throw new IllegalStateException(format, e);
        }
    }
}
