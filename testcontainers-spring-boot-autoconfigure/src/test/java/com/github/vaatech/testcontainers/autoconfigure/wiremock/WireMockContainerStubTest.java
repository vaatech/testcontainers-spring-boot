package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
public class WireMockContainerStubTest {

    @Autowired(required = false)
    WireMockConnectionDetails connectionDetails;

    @Autowired
    ConfigurableEnvironment environment;

    @Test
    public void propertiesAreAvailable() {
        assertThat(environment.getProperty("container.wiremock.baseUrl")).isNotEmpty();
        assertThat(environment.getProperty("container.wiremock.host")).isNotEmpty();
        assertThat(environment.getProperty("container.wiremock.port")).isNotEmpty();
    }

    @Test
    void shouldRequestWiremockStub() throws IOException, InterruptedException {
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
    }

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration({
            TestcontainersPropertySourceAutoConfiguration.class,
            ServiceConnectionAutoConfiguration.class,
            DockerPresenceAutoConfiguration.class,
            TestcontainersEnvironmentAutoConfiguration.class,
            WireMockConnectionAutoConfiguration.class,
            WireMockConnectionDetailsAutoConfiguration.class
    })
    static class TestConfiguration {

    }
}
