package com.github.vaatech.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(value = Ordered.LOWEST_PRECEDENCE)
@ConditionalOnContainersEnabled
public class TestcontainersEnvironmentAutoConfiguration {

    public static final String TESTCONTAINERS_ENVIRONMENT = "testcontainersEnvironment";

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnContainersEnabled
    static class NetworkConfiguration {

        @Bean
        @ConditionalOnMissingBean(Network.class)
        public Network network() {
            Network network = Network.newNetwork();
            log.info("Created docker Network with id={}", network.getId());
            return network;
        }
    }

//    @Bean(name = TESTCONTAINERS_ENVIRONMENT)
//    @ConditionalOnContainersEnabled
//    public TestcontainersEnvironment testcontainersEnvironment(ObjectProvider<GenericContainer<?>> containerProvider,
//                                                               final Network network) {
//
//        GenericContainer<?>[] containers = containerProvider == null
//                ? new GenericContainer<?>[0]
//                : containerProvider.stream().toArray(GenericContainer[]::new);
//
//        return new TestcontainersEnvironment(containers, network);
//    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnContainersEnabled
    @ConditionalOnClass(value = {GenericContainer.class, Network.class})
    static class DockerPresenceConfiguration {

        public static final String DOCKER_IS_AVAILABLE = "dockerPresenceMarker";

        @Bean(name = DOCKER_IS_AVAILABLE)
        public DockerPresenceMarker dockerPresenceMarker() {
            return new DockerPresenceMarker(DockerClientFactory.instance().isDockerAvailable());
        }

//        @Bean
//        public static DependsOnPostProcessor containerDependsOnDockerPostProcessor() {
//            return new DependsOnPostProcessor(GenericContainer.class, DOCKER_IS_AVAILABLE);
//        }

//        @Bean
//        public static DependsOnPostProcessor networkDependsOnDockerPostProcessor() {
//            return new DependsOnPostProcessor(Network.class, DOCKER_IS_AVAILABLE);
//        }
    }
}
