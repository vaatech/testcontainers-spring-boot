package io.github.vaatech.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(value = Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = "containers.enabled", matchIfMissing = true)
public class TestcontainersEnvironmentAutoConfiguration {

    public static final String TESTCONTAINERS_ENVIRONMENT = "testcontainersEnvironment";

    @Configuration(proxyBeanMethods = false)
    static class NetworkConfiguration {

        @Bean
        @ConditionalOnMissingBean(Network.class)
        public Network network() {
            Network network = Network.newNetwork();
            log.info("Created docker Network with id={}", network.getId());
            return network;
        }
    }

    @Bean(name = TESTCONTAINERS_ENVIRONMENT)
    public TestcontainersEnvironment testcontainersEnvironment(ObjectProvider<GenericContainer<?>> containerProvider,
                                                               final Network network) {

        GenericContainer<?>[] containers = containerProvider == null
                ? new GenericContainer<?>[0]
                : containerProvider.stream().toArray(GenericContainer[]::new);

        return new TestcontainersEnvironment(containers, network);
    }
}
