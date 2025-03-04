package com.github.vaatech.testcontainers.core;

import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.testcontainers.containers.GenericContainer;

public interface ContainerFactory {

    <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                      ParameterizedTypeReference<C> typeReference);

    <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                      Class<C> containerClass);

    <C extends GenericContainer<?>> C createContainer(CommonContainerProperties properties,
                                                      Class<C> containerClass,
                                                      Class<?> parameterType);
}
