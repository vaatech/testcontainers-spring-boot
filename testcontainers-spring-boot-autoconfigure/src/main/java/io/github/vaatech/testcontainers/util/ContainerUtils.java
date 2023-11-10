package io.github.vaatech.testcontainers.util;

import io.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class ContainerUtils {

    public static DockerImageName getDockerImageName(CommonContainerProperties properties) {
        CommonContainerProperties.DockerImage customDockerImage = properties.getDockerImage();
        String defaultDockerImageName = properties.getDefaultDockerImage().fullImageName();
        if (customDockerImage == null && defaultDockerImageName == null) {
            throw new IllegalStateException("Please specify dockerImage for the container.");
        }
        if (customDockerImage == null) {
            return setupImage(defaultDockerImageName, properties);
        }
        DockerImageName customImage = setupImage(customDockerImage.fullImageName(), properties);
        if (defaultDockerImageName == null) {
            return customImage;
        }
        DockerImageName defaultImage = DockerImageName.parse(defaultDockerImageName);
        log.warn(
                "Custom Docker image {} configured for the container. Note that it may not be compatible with the default Docker image {}.",
                customImage, defaultImage);
        return customImage.asCompatibleSubstituteFor(defaultImage);
    }

    private static DockerImageName setupImage(String imageName, CommonContainerProperties properties) {
        DockerImageName image = DockerImageName.parse(imageName);
        if (properties.getDockerImage() != null && properties.getDockerImage().getVersion() != null) {
            image = image.withTag(properties.getDockerImage().getVersion());
        }
        return image;
    }

    public static Consumer<OutputFrame> containerLogsConsumer(Logger log) {
        return of -> log.info(of.getUtf8String().trim());
    }

    public static void run(Startable... containers) {
        try {
            Startables.deepStart(containers).join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Container.ExecResult executeInContainer(
            ContainerState container, String... command) {
        try {
            return container.execInContainer(command);
        } catch (Exception e) {
            String format = String.format("Exception was thrown when executing: %s, for container: %s ",
                    Arrays.toString(command), container.getContainerId());
            throw new IllegalStateException(format, e);
        }
    }
}
