package com.github.vaatech.testcontainers.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

@Slf4j
@AutoConfiguration
@AutoConfigureOrder(value = Ordered.LOWEST_PRECEDENCE)
@ConditionalOnContainersEnabled
public class TestcontainersEnvironmentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Network.class)
    Network network() {
        Network network = Network.newNetwork();
        log.info("Created docker Network with id={}", network.getId());
        return network;
    }

    @Bean
    ContainerLogsBeanPostProcessor containerLogsBeanPostProcessor() {
        return new ContainerLogsBeanPostProcessor();
    }
}
