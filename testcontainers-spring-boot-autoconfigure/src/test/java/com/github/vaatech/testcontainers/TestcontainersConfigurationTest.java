package com.github.vaatech.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.Network;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersConfigurationTest {

    @Test
    public void whenContainersDisabledContextLoads() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(TestcontainersEnvironmentAutoConfiguration.class));

        contextRunner
                .withPropertyValues("containers.enabled=false")
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Network.class)
                        .doesNotHaveBean(DockerPresenceMarker.class));
    }

    @Test
    public void whenContainersEnabledContextLoads() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(TestcontainersEnvironmentAutoConfiguration.class));

        contextRunner
                .withPropertyValues("containers.enabled=true")
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .hasSingleBean(Network.class)
                        .hasSingleBean(DockerPresenceMarker.class));
    }
}
