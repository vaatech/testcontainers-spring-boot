package io.github.vaatech.testcontainers.wiremock;

import io.github.vaatech.testcontainers.ContainerCustomizers;
import io.github.vaatech.testcontainers.GenericContainerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;

@AutoConfiguration
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.wiremock.enabled", matchIfMissing = true)
@EnableConfigurationProperties(WireMockProperties.class)
public class WireMockContainerAutoconfiguration {

    @Bean(name = WireMockProperties.BEAN_NAME_CONTAINER_WIREMOCK, destroyMethod = "stop")
    WireMockContainer wireMockContainer(WireMockProperties properties,
                                        WireMockContainerCustomizers customizers) {

        WireMockContainer wireMockContainer = GenericContainerFactory.getGenericContainer(
                properties,
                new ParameterizedTypeReference<>() {},
                LoggerFactory.getLogger("container-wiremock")
        );

        return customizers.customize(wireMockContainer);
    }

    @Bean
    WireMockContainerCustomizer standardWireMockContainerCustomizer(WireMockProperties properties,
                                                                    DynamicPropertyRegistry registry) {
        return container -> {
            container
                    .withPort(properties.getPort())
                    .withBannerDisabled(properties.isDisableBanner())
                    .withVerbose(properties.isVerbose());

            registry.add("container.wiremock.host", container::getHost);
            registry.add("container.wiremock.port", () -> container.getMappedPort(properties.getPort()));
        };
    }

    @Bean
    WireMockContainerCustomizers wireMockContainerCustomizers(ObjectProvider<WireMockContainerCustomizer> customizers) {
        return new WireMockContainerCustomizers(customizers);
    }

    static class WireMockContainerCustomizers extends ContainerCustomizers<WireMockContainer, WireMockContainerCustomizer> {

        public WireMockContainerCustomizers(ObjectProvider<? extends WireMockContainerCustomizer> customizers) {
            super(customizers);
        }
    }

}
