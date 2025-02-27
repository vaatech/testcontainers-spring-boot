package com.github.vaatech.testcontainers.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.DockerLoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

@Slf4j
public class ContainerFactory {

    @SuppressWarnings("unchecked")
    public static <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                                    ParameterizedTypeReference<C> typeReference) {

        ResolvableType resolvable = ResolvableType.forType(typeReference);
        Class<C> containerClass = (Class<C>) resolvable.getRawClass();

        if (containerClass == null) {
            throw new RuntimeException("Cannot get raw type for " + typeReference.getType());
        }

        return createContainer(properties, containerClass);
    }

    public static <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                                    Class<C> containerClass) {
        try {
            Constructor<C> constructor = containerClass.getDeclaredConstructor(DockerImageName.class);
            constructor.setAccessible(true);
            C container = constructor.newInstance(getDockerImageName(properties));

            container
                    .withStartupTimeout(properties.getStartupTimeout())
                    .withStartupAttempts(properties.getStartupAttempts())
                    .withReuse(properties.isReuseContainer())
                    .withEnv(properties.getEnv())
                    .withLabels(properties.getLabel())
                    .withImagePullPolicy(properties.isUsePullAlwaysPolicy() ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy());

            if (properties.getCommand() != null && properties.getCommand().length > 0) {
                container.withCommand(properties.getCommand());
            }

            if (properties.isAttachContainerLog()) {
                var logger = logger(container);
                var logConsumer = containerLogsConsumer(logger);
                container.withLogConsumer(logConsumer);
            }

            return container;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create container " + containerClass, ex);
        }
    }

    private static DockerImageName getDockerImageName(CommonContainerProperties properties) {
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

    private static Logger logger(Container<?> container) {
        return DockerLoggerFactory.getLogger(container.getDockerImageName());
    }

    private static Consumer<OutputFrame> containerLogsConsumer(final Logger logger) {
        return new Slf4jLogConsumer(logger, true);
    }
}
