package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testcontainers.containers.Container;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class WireMockContainerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    TestcontainersPropertySourceAutoConfiguration.class,
                    ServiceConnectionAutoConfiguration.class,
                    DockerPresenceAutoConfiguration.class,
                    TestcontainersEnvironmentAutoConfiguration.class,
                    WireMockContainerAutoconfiguration.class,
                    WireMockConnectionDetailsAutoConfiguration.class));

    @Test
    public void contextLoads() {
        contextRunner
                .withPropertyValues(
                        "container.wiremock.enabled=false"
                )
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Container.class)
                        .doesNotHaveBean(WireMock.class));
    }

    @Test
    void shouldRegisterPropertiesToEnvironment() {
        contextRunner
                .run(context -> {
                    ConfigurableEnvironment environment = context.getEnvironment();
                    assertThat(environment).isNotNull();

                    final String wireMockHost = environment.getProperty("container.wiremock.host");
                    assertThat(wireMockHost).isNotEmpty();

                    final Integer wireMockPort = environment.getProperty("container.wiremock.port", Integer.class);
                    assertThat(wireMockPort).isNotNull();
                });
    }

    @Test
    void shouldRequestWiremockStub() {
        contextRunner
                .run(context -> {
                    ObjectProvider<WireMockConnectionDetails> connectionDetailsProvider = context.getBeanProvider(WireMockConnectionDetails.class);
                    var connectionDetails = connectionDetailsProvider.getIfAvailable();
                    assertThat(connectionDetails).isNotNull();

                    WireMock.stubFor(WireMock.get("/say-hello").willReturn(WireMock.ok("Hello world!")));
                    try (HttpClient client = HttpClient.newHttpClient()) {
                        var response = client.send(
                                HttpRequest.newBuilder()
                                        .uri(URI.create(String.format("http://%s:%d/say-hello", connectionDetails.host(), connectionDetails.port())))
                                        .build(),
                                HttpResponse.BodyHandlers.ofString());

                        assertThat(response.statusCode()).isEqualTo(200);
                        assertThat(response.body()).isEqualTo("Hello world!");
                    }
                });
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

    @Test
    public void propertiesAreAvailable() {
        contextRunner
                .run(context -> {
                    var environment = context.getEnvironment();
                    assertThat(environment.getProperty("container.wiremock.baseUrl")).isNotEmpty();
                    assertThat(environment.getProperty("container.wiremock.host")).isNotEmpty();
                    assertThat(environment.getProperty("container.wiremock.port")).isNotEmpty();
                });
    }

}