package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.vaatech.testcontainers.wiremock.WireMockProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.Container;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class WireMockContainerDisabledTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    WireMockConnectionAutoConfiguration.class,
                    WireMockConnectionDetailsAutoConfiguration.class));

    @Test
    public void contextLoads() {
        contextRunner
                .withPropertyValues(
                        "container.wiremock.enabled=false"
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
                    ObjectProvider<WireMockConnectionDetails> connectionDetailsProvider = context.getBeanProvider(WireMockConnectionDetails.class);
                    assertThat(connectionDetailsProvider).isNotNull();

                    var connectionDetails = connectionDetailsProvider.getIfAvailable();
                    assertThat(connectionDetails).isNotNull();

                    WireMockProperties wireMockProperties = context.getBean(WireMockProperties.class);

                    assertThat(wireMockProperties).isNotNull();

                    assertThat(wireMockProperties.getBaseUrl()).isEqualTo(connectionDetails.url());
                    assertThat(wireMockProperties.getHost()).isEqualTo(connectionDetails.host());
                    assertThat(wireMockProperties.getPort()).isEqualTo(connectionDetails.port());
                });
    }
}