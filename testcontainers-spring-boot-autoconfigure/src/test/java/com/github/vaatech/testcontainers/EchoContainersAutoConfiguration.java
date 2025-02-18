package com.github.vaatech.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.springframework.boot.autoconfigure.AutoConfigureOrder.DEFAULT_ORDER;

@Slf4j
@Configuration
@AutoConfigureOrder(DEFAULT_ORDER)
public class EchoContainersAutoConfiguration {

    @Bean
    @SuppressWarnings("resource")
    EchoContainer echoContainer1(List<ContainerCustomizer<EchoContainer>> customizers) {
        EchoContainer echoContainer = new EchoContainer(DockerImageName.parse("alpine:3.18.4"));
        customizers.forEach(c -> c.customize(echoContainer));
        return echoContainer;
    }

    @Bean
    @SuppressWarnings("resource")
    EchoContainer echoContainer2(List<ContainerCustomizer<EchoContainer>> customizers) {
        EchoContainer echoContainer = new EchoContainer(DockerImageName.parse("alpine:3.18.4"));
        customizers.forEach(c -> c.customize(echoContainer));
        return echoContainer;
    }

    @Bean
    ContainerCustomizer<EchoContainer> echoContainerCustomizer() {
        return container -> container
                .withCommand("/bin/sh", "-c", "while true; do echo 'Press [CTRL+C] to stop..'; sleep 1; done")
                .withStartupTimeout(Duration.ofSeconds(15));
    }
}
