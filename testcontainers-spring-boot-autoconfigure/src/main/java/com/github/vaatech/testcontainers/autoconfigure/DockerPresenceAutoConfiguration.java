package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

@AutoConfiguration
@AutoConfigureOrder(value = Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnContainersEnabled
@ConditionalOnClass(value = {GenericContainer.class, Network.class})
public class DockerPresenceAutoConfiguration {

    public static final String DOCKER_IS_AVAILABLE = "dockerPresenceMarker";

    @Bean(name = DOCKER_IS_AVAILABLE)
    public DockerPresenceMarker dockerPresenceMarker() {
        return new DockerPresenceMarker(DockerClientFactory.instance().isDockerAvailable());
    }

    @Bean
    public static DependsOnDockerBeanFactoryPostProcessor containerDependsOnDockerPostProcessor() {
        return new DependsOnDockerBeanFactoryPostProcessor(GenericContainer.class);
    }

    @Bean
    public static DependsOnDockerBeanFactoryPostProcessor networkDependsOnDockerPostProcessor() {
        return new DependsOnDockerBeanFactoryPostProcessor(Network.class);
    }
}