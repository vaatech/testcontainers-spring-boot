package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.ContainerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.Network;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static com.github.vaatech.testcontainers.autoconfigure.wiremock.WireMockProperties.BEAN_NAME_CONTAINER_WIREMOCK;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({WireMock.class, WireMockContainer.class})
@ConditionalOnMissingBean(WireMockContainer.class)
@ConditionalOnWireMockContainerEnabled
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockContainerConfiguration {

    private static final String WIREMOCK_NETWORK_ALIAS = "wiremock.testcontainer.docker";

    @ServiceConnection(type = WireMockConnectionDetails.class)
    @Bean(name = BEAN_NAME_CONTAINER_WIREMOCK, destroyMethod = "stop")
    WireMockContainer wiremock(final WireMockProperties properties,
                               final ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>> customizers) {

        WireMockContainer wireMockContainer = ContainerFactory.createContainer(properties, WireMockContainer.class);
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
            wiremock.withExtraHost(DEFAULT_DNS_NAME, "host-gateway");
            network.ifPresent(wiremock::withNetwork);
        };
    }

    @Bean
    ContainerCustomizers<WireMockContainer, ContainerCustomizer<WireMockContainer>>
    wireMockContainerCustomizers(final ObjectProvider<ContainerCustomizer<WireMockContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }
}
