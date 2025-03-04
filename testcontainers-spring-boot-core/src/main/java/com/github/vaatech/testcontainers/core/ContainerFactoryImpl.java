package com.github.vaatech.testcontainers.core;

import com.github.dockerjava.api.model.Capability;
import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
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
import org.testcontainers.utility.MountableFile;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.vaatech.testcontainers.core.config.CommonContainerProperties.*;

@Slf4j
public class ContainerFactoryImpl implements ContainerFactory {

    @SuppressWarnings("unchecked")
    public <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                             ParameterizedTypeReference<C> typeReference) {

        ResolvableType resolvable = ResolvableType.forType(typeReference);
        Class<C> containerClass = (Class<C>) resolvable.getRawClass();

        if (containerClass == null) {
            throw new RuntimeException("Cannot get raw type for " + typeReference.getType());
        }

        return createContainer(properties, containerClass);
    }

    public <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
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

            for (MountVolume mountVolume : properties.getMountVolumes()) {
                MountableFile mountableFile = switch (mountVolume.type()) {
                    case CLASSPATH -> {
                        if (mountVolume.mode() != null) {
                            yield MountableFile.forClasspathResource(mountVolume.path(), mountVolume.mode());
                        }
                        yield MountableFile.forClasspathResource(mountVolume.path());
                    }
                    case HOST -> {
                        if (mountVolume.mode() != null) {
                            yield MountableFile.forHostPath(mountVolume.path(), mountVolume.mode());
                        }
                        yield MountableFile.forHostPath(mountVolume.path());
                    }
                };
                container.withCopyToContainer(mountableFile, mountVolume.containerPath());
            }

            if (!properties.getCapabilities().isEmpty()) {
                Capability[] capabilities = properties.getCapabilities().toArray(new Capability[0]);
                container.withCreateContainerCmdModifier(
                        cmd -> Objects.requireNonNull(cmd.getHostConfig()).withCapAdd(capabilities));
            }

            if (!properties.getTmpFs().isEmpty()) {
                Map<String, String> tmpFsMapping = properties.getTmpFs()
                        .stream()
                        .collect(Collectors.toMap(TmpFsMount::folder, TmpFsMount::options));
                container.withTmpFs(tmpFsMapping);
            }

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
        DockerImage customDockerImage = properties.getDockerImage();
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
