package com.github.vaatech.testcontainers.autoconfigure;

import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.core.ContainerFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.testcontainers.containers.Network;

@Slf4j
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnContainersEnabled
public class TestcontainersEnvironmentAutoConfiguration {

    public static final String DEFAULT_DNS_NAME = "host.docker.internal";

    @Bean
    @ConditionalOnMissingBean(Network.class)
    Network network() {
        Network network = Network.newNetwork();
        log.info("Created docker Network with id={}", network.getId());
        return network;
    }

    @Bean
    @ConditionalOnMissingBean
    public ContainerFactory containerFactory() {
        return new ContainerFactoryImpl();
    }
}
