package com.github.vaatech.testcontainers.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

        try {
            Constructor<C> constructor = containerClass.getDeclaredConstructor(DockerImageName.class);
            constructor.setAccessible(true);
            C container = constructor.newInstance(getDockerImageName(properties));

            container
                    .withStartupTimeout(properties.getTimeoutDuration())
                    .withReuse(properties.isReuseContainer())
                    .withImagePullPolicy(properties.isUsePullAlwaysPolicy() ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy());

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
}
