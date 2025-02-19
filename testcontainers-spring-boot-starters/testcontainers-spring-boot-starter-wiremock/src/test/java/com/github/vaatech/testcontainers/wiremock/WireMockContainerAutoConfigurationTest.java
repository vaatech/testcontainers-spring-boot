package com.github.vaatech.testcontainers.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
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
class WireMockContainerAutoConfigurationTest {

    @Nested
    @DisplayName("AutoConfigured WireMock Container")
    @SpringBootTest(
            classes = WireMockContainerAutoConfigurationTest.TestConfiguration.class,
            properties = {
                    "container.wiremock.enabled=true"
            }
    )
    class DefaultTests {
        @Autowired
        ConfigurableEnvironment environment;

        @Value("${container.wiremock.host}")
        String wiremockHost;

        @Value("${container.wiremock.port}")
        int wiremockPort;

        @Autowired
        ObjectProvider<WireMockConnectionDetails> connectionDetails;

        @BeforeEach
        void setUp() {
            WireMock.configureFor(wiremockHost, wiremockPort);
        }

        @Test
        void shouldRequestWiremockStub() throws IOException, InterruptedException {
            var details = connectionDetails.getIfAvailable();
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

        @Nested
        @DisplayName("AutoConfigured Default Connection Details")
        @SpringBootTest(
                classes = WireMockContainerAutoConfigurationTest.TestConfiguration.class,
                properties = {
                        "containers.enabled=false"
                }
        )
        class ContainersDisabledTest {

            @Autowired
            ObjectProvider<WireMockConnectionDetails> connectionDetailsProvider;

            @Autowired
            WireMockProperties wireMockProperties;

            @Test
            void shouldHaveDefaultConnectionDetails() {
                assertThat(connectionDetailsProvider).isNotNull();
                var connectionDetails = connectionDetailsProvider.getIfAvailable();
                assertThat(connectionDetails).isNotNull();
                assertThat(wireMockProperties).isNotNull();

                assertThat(wireMockProperties.getBaseUrl()).isEqualTo(connectionDetails.url());
                assertThat(wireMockProperties.getHost()).isEqualTo(connectionDetails.host());
                assertThat(wireMockProperties.getPort()).isEqualTo(connectionDetails.port());
            }
        }
    }

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }
}