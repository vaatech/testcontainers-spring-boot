package io.github.vaatech.testcontainers.toxiproxy;


import io.github.vaatech.testcontainers.ContainerCustomizers;
import io.github.vaatech.testcontainers.util.ContainerUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;

import static io.github.vaatech.testcontainers.toxiproxy.ToxiproxyProperties.BEAN_NAME_CONTAINER_TOXIPOROXY;

@Slf4j
@AutoConfiguration
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.toxiproxy.enabled", matchIfMissing = true)
@EnableConfigurationProperties(ToxiproxyProperties.class)
public class ToxiproxyContainerAutoConfiguration {

    private static final String TOXIPROXY_NETWORK_ALIAS = "toxiproxy.testcontainer.docker";
    // keep the old alias for backward compatibility
    private static final String TOXIPROXY_NETWORK_ALIAS_OLD = "toxiproxy";

    @Bean
    @ConditionalOnMissingBean(Network.class)
    Network toxiproxyNetwork() {
        Network network = Network.newNetwork();
        log.info("Created docker Network with id={}", network.getId());
        return network;
    }

    @Bean(name = BEAN_NAME_CONTAINER_TOXIPOROXY, destroyMethod = "stop")
    ToxiproxyContainer toxiproxy(ToxiproxyProperties toxiProxyProperties,
                                 ToxiproxyContainerCustomizers toxiProxyContainerCustomizers) {
        ToxiproxyContainer toxiproxyContainer = new ToxiproxyContainer(ContainerUtils.getDockerImageName(toxiProxyProperties));
        return toxiProxyContainerCustomizers.customize(toxiproxyContainer);
    }

    @Bean
    @Order(0)
    ToxiproxyContainerCustomizer standardToxiproxyContainerCustomizer(final Network network,
                                                                      DynamicPropertyRegistry registry) {
        return container -> {

            container
                    .withNetwork(network)
                    .withNetworkAliases(TOXIPROXY_NETWORK_ALIAS, TOXIPROXY_NETWORK_ALIAS_OLD)
                    .withLogConsumer(ContainerUtils.containerLogsConsumer(LoggerFactory.getLogger("container-toxiproxy")));

            registry.add("container.toxiproxy.host", container::getHost);
            registry.add("container.toxiproxy.controlPort", container::getControlPort);
            registry.add("container.toxiproxy.networkAlias", () -> TOXIPROXY_NETWORK_ALIAS);
        };
    }

    @Bean
    ToxiproxyContainerCustomizers toxiProxyContainerCustomizers(ObjectProvider<ToxiproxyContainerCustomizer> customizers) {
        return new ToxiproxyContainerCustomizers(customizers);
    }

    static public class ToxiproxyContainerCustomizers extends ContainerCustomizers<ToxiproxyContainer, ToxiproxyContainerCustomizer> {
        public ToxiproxyContainerCustomizers(ObjectProvider<? extends ToxiproxyContainerCustomizer> customizers) {
            super(customizers);
        }
    }
}
