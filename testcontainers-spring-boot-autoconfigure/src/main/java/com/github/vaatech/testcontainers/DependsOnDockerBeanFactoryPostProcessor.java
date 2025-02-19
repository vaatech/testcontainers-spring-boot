package com.github.vaatech.testcontainers;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

import static com.github.vaatech.testcontainers.DockerPresenceAutoConfiguration.DOCKER_IS_AVAILABLE;

public class DependsOnDockerBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {

    public DependsOnDockerBeanFactoryPostProcessor(Class<?> beansOfType) {
        super(beansOfType, DOCKER_IS_AVAILABLE);
    }
}
