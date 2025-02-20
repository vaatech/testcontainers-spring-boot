package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

import static com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration.DOCKER_IS_AVAILABLE;

public class DependsOnDockerBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {

    public DependsOnDockerBeanFactoryPostProcessor(Class<?> beansOfType) {
        super(beansOfType, DOCKER_IS_AVAILABLE);
    }
}
