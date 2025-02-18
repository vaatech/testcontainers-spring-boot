package com.github.vaatech.testcontainers.mailpit;

import com.github.vaatech.testcontainers.ContainerCustomizer;
import com.github.vaatech.testcontainers.ContainerCustomizers;
import com.github.vaatech.testcontainers.GenericContainerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Objects;
import java.util.Optional;

import static com.github.vaatech.testcontainers.mailpit.MailPitProperties.BEAN_NAME_CONTAINER_MAILPIT;

@AutoConfiguration
@ConditionalOnMailPitContainerEnabled
@EnableConfigurationProperties(value = {MailPitProperties.class})
public class MailPitContainerAutoConfiguration {

    private static final String MAILPIT_NETWORK_ALIAS = "mailpit.testcontainer.docker";

    @Bean(name = BEAN_NAME_CONTAINER_MAILPIT, destroyMethod = "stop")
    public MailPitContainer mailpit(MailPitProperties properties,
                                    ContainerCustomizers<MailPitContainer, ContainerCustomizer<MailPitContainer>> customizers) {

        MailPitContainer mailpit = GenericContainerFactory.getGenericContainer(
                properties,
                new ParameterizedTypeReference<>() {
                },
                LoggerFactory.getLogger("container-mailpit")
        );

        return customizers.customize(mailpit);
    }

    @Bean
    @Order(0)
    ContainerCustomizer<MailPitContainer> mailPitContainerCustomizer(final MailPitProperties properties,
                                                                     final DynamicPropertyRegistry registry,
                                                                     final Optional<Network> network) {
        return mailpit -> {
            if (properties.isVerbose()) {
                mailpit.withEnv("MP_VERBOSE", "true");
            }

            mailpit.withEnv("MP_MAX_MESSAGES", Objects.toString(properties.getMaxMessages()));
            mailpit.waitingFor(Wait.forLogMessage(".*accessible via.*", 1));
            mailpit.addExposedPorts(properties.getPortHttp(), properties.getPortSmtp());
            mailpit.withNetworkAliases(MAILPIT_NETWORK_ALIAS);

            network.ifPresent(mailpit::withNetwork);

            registry.add("container.mailpit.host", mailpit::getHost);
            registry.add("container.mailpit.port-http", () -> mailpit.getMappedPort(properties.getPortHttp()));
            registry.add("container.mailpit.port-smtp", () -> mailpit.getMappedPort(properties.getPortSmtp()));
            registry.add("container.mailpit.server", () -> String.format("http://%s:%d",
                    mailpit.getHost(), mailpit.getMappedPort(properties.getPortHttp())));
        };
    }

    @Bean
    ContainerCustomizers<MailPitContainer, ContainerCustomizer<MailPitContainer>>
    mailPitContainerCustomizers(ObjectProvider<ContainerCustomizer<MailPitContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }
}
