package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.vaatech.testcontainers.autoconfigure.ContainerConfigurer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.wiremock.WireMockProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static com.github.vaatech.testcontainers.wiremock.WireMockProperties.BEAN_NAME_CONTAINER_WIREMOCK;

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
                               final ContainerFactory containerFactory,
                               final ContainerConfigurer configurer,
                               final ObjectProvider<ContainerCustomizer<WireMockContainer>> customizers) {

        WireMockContainer wireMockContainer = containerFactory.createContainer(properties, WireMockContainer.class);
        return configurer.configure(wireMockContainer, properties, customizers.orderedStream());
    }

    @Bean
    @Order(0)
    ContainerCustomizer<WireMockContainer> standardWireMockContainerCustomizer(final WireMockProperties properties) {
        return wiremock -> {
            if (properties.isVerbose()) {
                wiremock.withCliArg("--verbose");
            }
            wiremock.withoutBanner();
            wiremock.withNetworkAliases(WIREMOCK_NETWORK_ALIAS);
        };
    }
}
