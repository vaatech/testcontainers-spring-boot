package io.github.vaatech.testcontainers.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(
        classes = WireMockContainerAutoconfigurationTest.TestConfiguration.class,
        properties = {
                "container.wiremock.enabled=true"
        }
)
class WireMockContainerAutoconfigurationTest {
    @Autowired
    ConfigurableEnvironment environment;

    @Value("${container.wiremock.host}")
    String wiremockHost;

    @Value("${container.wiremock.port}")
    int wiremockPort;

    @BeforeEach
    void setUp() {
        WireMock.configureFor(wiremockHost, wiremockPort);
    }

    @Test
    void shouldRequestWiremockStub() throws IOException, InterruptedException {
        stubFor(get("/say-hello").willReturn(ok("Hello world!")));

        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(String.format("http://%s:%d/say-hello", wiremockHost, wiremockPort)))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello world!");
    }

    @Test
    public void propertiesAreAvailable() {
        assertThat(environment.getProperty("container.wiremock.port")).isNotEmpty();
        assertThat(environment.getProperty("container.wiremock.host")).isNotEmpty();
    }

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }
}