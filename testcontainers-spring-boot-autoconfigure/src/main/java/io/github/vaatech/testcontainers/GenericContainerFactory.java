package io.github.vaatech.testcontainers;

import io.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class GenericContainerFactory {


    @SuppressWarnings("unchecked")
    public static <C extends GenericContainer<?>> C getGenericContainer(CommonContainerProperties properties,
                                                                        ParameterizedTypeReference<C> typeReference,
                                                                        Logger logger) {

        try {
            ResolvableType resolvable = ResolvableType.forType(typeReference);
            Class<C> rawType = (Class<C>) resolvable.getRawClass();

            if (rawType == null) {
                throw new RuntimeException("Cannot get raw type for " + typeReference.getType());
            }

            Constructor<C> constructor = rawType.getConstructor(DockerImageName.class);
            C container = constructor.newInstance(getDockerImageName(properties));

            container
                    .withStartupTimeout(properties.getTimeoutDuration())
                    .withReuse(properties.isReuseContainer())
                    .withLogConsumer(new Slf4jLogConsumer(logger, true))
                    .withImagePullPolicy(properties.isUsePullAlwaysPolicy() ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy());

            return container;
        } catch (NoSuchMethodException
                 | InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException e) {
            throw new RuntimeException(e);
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
