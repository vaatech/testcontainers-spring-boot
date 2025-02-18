package com.github.vaatech.testcontainers.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.ContainerCustomizer;
import com.github.vaatech.testcontainers.ContainerCustomizers;
import com.github.vaatech.testcontainers.GenericContainerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Optional;

import static com.github.vaatech.testcontainers.wiremock.WireMockProperties.BEAN_NAME_CONTAINER_WIREMOCK;


@AutoConfiguration
@ConditionalOnWireMockContainerEnabled
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockContainerAutoconfiguration {

    private static final String WIREMOCK_NETWORK_ALIAS = "wiremock.testcontainer.docker";


    @Bean(name = BEAN_NAME_CONTAINER_WIREMOCK, destroyMethod = "stop")
    WireMockContainer wiremock(final WireMockProperties properties,
                               final ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>> customizers) {

        WireMockContainer wireMockContainer = GenericContainerFactory.getGenericContainer(
                properties,
                new ParameterizedTypeReference<>() {
                },
                LoggerFactory.getLogger("container-wiremock")
        );

        return customizers.customize(wireMockContainer);
    }

    @Bean
    @ConditionalOnMissingBean(WireMock.class)
    WireMock wireMock(@WireMockHost String wireMockHost,
                      @WireMockPort Integer wireMockPort) {

        WireMock wireMock = new WireMock(wireMockHost, wireMockPort);
        WireMock.configureFor(wireMock);
        return wireMock;
    }

    @Bean
    @Order(0)
    ContainerCustomizer<WireMockContainer> standardWireMockContainerCustomizer(final WireMockProperties properties,
                                                                               final DynamicPropertyRegistry registry,
                                                                               final Optional<Network> network) {
        return wiremock -> {
            if (properties.isVerbose()) {
                wiremock.withCliArg("--verbose");
            }

            wiremock.withoutBanner();
            wiremock.withNetworkAliases(WIREMOCK_NETWORK_ALIAS);

            network.ifPresent(wiremock::withNetwork);

            registry.add("container.wiremock.baseUrl", wiremock::getBaseUrl);
            registry.add("container.wiremock.host", wiremock::getHost);
            registry.add("container.wiremock.port", wiremock::getPort);
        };
    }

    @Bean
    ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>>
    wireMockContainerCustomizers(ObjectProvider<ContainerCustomizer<WireMockContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }
}
