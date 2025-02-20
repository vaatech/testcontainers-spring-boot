package com.github.vaatech.testcontainers.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.Network;

import static org.assertj.core.api.Assertions.assertThat;

class TestcontainersEnvironmentAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DockerPresenceAutoConfiguration.class,
                    TestcontainersEnvironmentAutoConfiguration.class));

    @Test
    void whenContainersDisabledContextLoads() {
        contextRunner
                .withPropertyValues("containers.enabled=false")
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Network.class)
                        .doesNotHaveBean(DockerPresenceMarker.class));
    }

    @Test
    void whenContainersEnabledContextLoads() {
        contextRunner
                .withPropertyValues("containers.enabled=true")
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(Network.class)
                        .hasSingleBean(DockerPresenceMarker.class));
    }
}
