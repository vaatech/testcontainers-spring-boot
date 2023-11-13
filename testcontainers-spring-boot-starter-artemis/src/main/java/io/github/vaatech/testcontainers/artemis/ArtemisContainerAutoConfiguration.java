package io.github.vaatech.testcontainers.artemis;

import io.github.vaatech.testcontainers.ContainerCustomizers;
import io.github.vaatech.testcontainers.DependsOnPostProcessor;
import io.github.vaatech.testcontainers.util.ContainerUtils;
import jakarta.jms.ConnectionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import static io.github.vaatech.testcontainers.artemis.ArtemisProperties.BEAN_NAME_CONTAINER_ARTEMIS;

@AutoConfiguration
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.artemis.enabled", matchIfMissing = true)
@EnableConfigurationProperties(ArtemisProperties.class)
public class ArtemisContainerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    static class ArtemisContainerDependencyConfiguration {
        @Bean
        public static BeanFactoryPostProcessor connectionFactoryDependencyPostProcessor() {
            return new DependsOnPostProcessor(ConnectionFactory.class, BEAN_NAME_CONTAINER_ARTEMIS);
        }
    }

    @Bean(name = ArtemisProperties.BEAN_NAME_CONTAINER_ARTEMIS, destroyMethod = "stop")
    ArtemisContainer artemisContainer(ArtemisProperties artemisProperties,
                                      ArtemisContainerCustomizers customizers) {

        ArtemisContainer container = new ArtemisContainer(ContainerUtils.getDockerImageName(artemisProperties));
        return customizers.customize(container);
    }

    @Bean
    @Order(0)
    ArtemisContainerCustomizer standardArtemisContainerCustomizer(ArtemisProperties properties) {
        return container -> container
                .withUser(properties.getUsername())
                .withPassword(properties.getPassword())
                .withAnonymousLogin(properties.isAllowAnonymousLogin())
                .withLogConsumer(ContainerUtils.containerLogsConsumer(LoggerFactory.getLogger("container-artemis")));
    }

    @Bean
    ArtemisContainerCustomizers artemisContainerCustomizers(ObjectProvider<ArtemisContainerCustomizer> customizers) {
        return new ArtemisContainerCustomizers(customizers);
    }

    static class ArtemisContainerCustomizers extends ContainerCustomizers<ArtemisContainer, ArtemisContainerCustomizer> {
        public ArtemisContainerCustomizers(ObjectProvider<? extends ArtemisContainerCustomizer> customizers) {
            super(customizers);
        }
    }
}
