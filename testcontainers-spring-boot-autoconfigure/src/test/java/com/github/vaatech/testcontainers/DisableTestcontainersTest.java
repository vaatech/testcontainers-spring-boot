package com.github.vaatech.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class DisableTestcontainersTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(TestcontainersEnvironmentAutoConfiguration.class));

    @Test
    public void contextLoads() {
        contextRunner
                .withPropertyValues("containers.enabled=false")
                .run((context) -> assertThat(context)
                        .hasNotFailed());
    }
}
