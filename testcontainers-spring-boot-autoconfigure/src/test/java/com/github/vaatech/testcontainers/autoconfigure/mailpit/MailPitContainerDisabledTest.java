package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.mailpit.MailPitProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.Container;

import static org.assertj.core.api.Assertions.assertThat;


class MailPitContainerDisabledTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    MailPitConnectionAutoConfiguration.class,
                    MailPitConnectionDetailsAutoConfiguration.class));

    @Test
    void contextLoads() {
        contextRunner
                .withPropertyValues(
                        "container.mailpit.enabled=false"
                )
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Container.class));
    }

    @Test
    void shouldHaveDefaultConnectionDetails() {
        contextRunner
                .withPropertyValues(
                        "containers.enabled=false"
                )
                .run(context -> {
                    var connectionDetailsProvider = context.getBeanProvider(MailPitConnectionDetails.class);
                    assertThat(connectionDetailsProvider).isNotNull();

                    var connectionDetails = connectionDetailsProvider.getIfAvailable();
                    assertThat(connectionDetails).isNotNull();

                    var wireMockProperties = context.getBean(MailPitProperties.class);

                    assertThat(wireMockProperties).isNotNull();

                    assertThat(wireMockProperties.getPortHttp()).isEqualTo(connectionDetails.portHttp());
                    assertThat(wireMockProperties.getPortSmtp()).isEqualTo(connectionDetails.portSMTP());
                    assertThat(wireMockProperties.getHost()).isEqualTo(connectionDetails.host());
                });
    }
}
