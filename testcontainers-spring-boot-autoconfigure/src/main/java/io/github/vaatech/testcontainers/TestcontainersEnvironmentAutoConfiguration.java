package io.github.vaatech.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

        return new TestcontainersEnvironment(containers, network, (containerStates) -> {
            for (ContainerState containerState : containerStates) {
                logContainerInfo(containerState.getContainerId(), (GenericContainer<?>) containerState);
            }
        });
    }

    private static void logContainerInfo(String name, GenericContainer<?> container) {
        String offset = "";
        log.info("{}{}:", offset, name);
        List<Integer> exposedPorts = new ArrayList<>(container.getExposedPorts());
        exposedPorts.sort(Comparator.naturalOrder());

        offset += "\t";
        log.info("{}Host: {}", offset, container.getHost());
        if (!exposedPorts.isEmpty()) {
            log.info("{}Ports:", offset);
        }

        offset += "\t";
        for (Integer port : exposedPorts) {
            Integer mappedPort = container.getMappedPort(port);
            log.info("{}{} -> {}", offset, port, Objects.toString(mappedPort, "NONE"));
        }
    }
}
