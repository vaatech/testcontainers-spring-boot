package com.github.vaatech.testcontainers.autoconfigure;

import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

import static com.github.vaatech.testcontainers.autoconfigure.ContainerFactoryTest.EchoContainer.COMMAND;
import static com.github.vaatech.testcontainers.autoconfigure.ContainerFactoryTest.EchoContainer.ENV;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
public class ContainerFactoryTest {

    @Autowired
    EchoContainer echoContainer;

    @Test
    void shouldStartContainer() {
        assertThat(echoContainer.isRunning()).isTrue();
        assertThat(echoContainer.getCommandParts()).isEqualTo(COMMAND);
        assertThat(echoContainer.getEnvMap()).containsAllEntriesOf(ENV);
        assertThat(echoContainer.getLogConsumers()).hasSize(1);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnContainersEnabled
    @EnableConfigurationProperties(EchoProperties.class)
    @ImportAutoConfiguration({
            TestcontainersPropertySourceAutoConfiguration.class,
            ServiceConnectionAutoConfiguration.class,
            DockerPresenceAutoConfiguration.class,
            TestcontainersEnvironmentAutoConfiguration.class,
    })
    static class TestConfiguration {

        @Bean(destroyMethod = "stop")
        EchoContainer echoContainer(EchoProperties echoProperties,
                                    ContainerFactory containerFactory) {
            return containerFactory.createContainer(echoProperties, EchoContainer.class);
        }
    }

    static class EchoContainer extends GenericContainer<EchoContainer> {

        static final String[] COMMAND = {"/bin/sh", "-c", "while true; do echo 'Press [CTRL+C] to stop..'; sleep 1; done"};

        static final Map<String, String> ENV = Map.of(
                "ENV_ONE", "VALUE_ONE",
                "ENV_TWO", "VALUE_TWO"
        );

        public EchoContainer(@NonNull final DockerImageName dockerImageName) {
            super(dockerImageName);
        }

        @Override
        protected void configure() {
            setCommand(COMMAND);
            getWaitStrategy().withStartupTimeout(Duration.ofSeconds(15));
            ENV.forEach(this::addEnv);
        }
    }

    @ConfigurationProperties("container.echo")
    static class EchoProperties extends CommonContainerProperties {

        @Override
        public DockerImage getDefaultDockerImage() {
            return DockerImage.create("alpine:3");
        }

        public EchoProperties() {
            setAttachContainerLog(true);
        }
    }
}
