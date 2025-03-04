package com.github.vaatech.testcontainers.autoconfigure;

import com.github.dockerjava.api.model.Capability;
import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import org.slf4j.Logger;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerLoggerFactory;
import org.testcontainers.utility.MountableFile;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static com.github.vaatech.testcontainers.core.config.CommonContainerProperties.*;

public class DefaultContainerConfigurer implements ContainerConfigurer {

    private final Network network;

    public DefaultContainerConfigurer(Network network) {
        this.network = network;
    }

    @Override
    public <C extends GenericContainer<?>> C configure(C container, CommonContainerProperties properties) {
        return configure(container, properties, Stream.empty());
    }

    public <C extends GenericContainer<?>> C configure(final C container,
                                                       final CommonContainerProperties properties,
                                                       final Stream<ContainerCustomizer<C>> customizers) {
        container
                .withStartupTimeout(properties.getStartupTimeout())
                .withStartupAttempts(properties.getStartupAttempts())
                .withReuse(properties.isReuseContainer())
                .withEnv(properties.getEnv())
                .withLabels(properties.getLabel())
                .withImagePullPolicy(properties.isUsePullAlwaysPolicy() ? PullPolicy.alwaysPull() : PullPolicy.defaultPolicy())
                .withExtraHost(DEFAULT_DNS_NAME, "host-gateway")
                .withNetwork(network);

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

        if (customizers != null) {
            customizers.forEach(it -> it.customize(container));
        }

        return container;
    }

    private static Logger logger(Container<?> container) {
        return DockerLoggerFactory.getLogger(container.getDockerImageName());
    }

    private static Consumer<OutputFrame> containerLogsConsumer(final Logger logger) {
        return new Slf4jLogConsumer(logger, true);
    }
}
