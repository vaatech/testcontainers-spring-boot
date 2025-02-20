package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.GenericContainerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.Network;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.wiremock.WireMockProperties.BEAN_NAME_CONTAINER_WIREMOCK;

@AutoConfiguration(
        before = ServiceConnectionAutoConfiguration.class,
        after = DockerPresenceAutoConfiguration.class)
@ConditionalOnWireMockContainerEnabled
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockContainerAutoconfiguration {

    private static final String WIREMOCK_NETWORK_ALIAS = "wiremock.testcontainer.docker";

    @ServiceConnection
    @Bean(name = BEAN_NAME_CONTAINER_WIREMOCK, destroyMethod = "stop")
    WireMockContainer wiremock(final WireMockProperties properties,
                               final ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>> customizers) {

        final var typeParam = new ParameterizedTypeReference<WireMockContainer>() {
        };
        WireMockContainer wireMockContainer = GenericContainerFactory.getGenericContainer(properties, typeParam);
        return customizers.customize(wireMockContainer);
    }

    @Bean
    @Order(0)
    ContainerCustomizer<WireMockContainer> standardWireMockContainerCustomizer(final WireMockProperties properties,
                                                                               final Optional<Network> network) {
        return wiremock -> {
            if (properties.isVerbose()) {
                wiremock.withCliArg("--verbose");
            }

            wiremock.withoutBanner();
            wiremock.withNetworkAliases(WIREMOCK_NETWORK_ALIAS);

            network.ifPresent(wiremock::withNetwork);
        };
    }

    @Bean
    ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>>
    wireMockContainerCustomizers(final ObjectProvider<ContainerCustomizer<WireMockContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }

    @Bean
    DynamicPropertyRegistrar wireMockContainerProperties(final WireMockContainer wiremock) {
        return registry -> {
            registry.add("container.wiremock.baseUrl", wiremock::getBaseUrl);
            registry.add("container.wiremock.host", wiremock::getHost);
            registry.add("container.wiremock.port", wiremock::getPort);
        };
    }

    @Bean
    @ConditionalOnMissingBean(WireMock.class)
    WireMock wireMock(final WireMockConnectionDetails connectionDetails) {
        WireMock wireMock = new WireMock(connectionDetails.host(), connectionDetails.port());
        WireMock.configureFor(wireMock);
        return wireMock;
    }

}
