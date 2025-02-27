package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.ContainerFactory;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Objects;
import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.mailpit.MailPitProperties.BEAN_NAME_CONTAINER_MAILPIT;

@AutoConfiguration(
        before = ServiceConnectionAutoConfiguration.class,
        after = DockerPresenceAutoConfiguration.class)
@ConditionalOnMailPitContainerEnabled
@EnableConfigurationProperties(value = {MailPitProperties.class})
public class MailPitContainerAutoConfiguration {

    private static final String MAILPIT_NETWORK_ALIAS = "mailpit.testcontainer.docker";

    @ServiceConnection
    @Bean(name = BEAN_NAME_CONTAINER_MAILPIT, destroyMethod = "stop")
    public MailPitContainer
    mailpit(MailPitProperties properties,
            ContainerCustomizers<MailPitContainer, ContainerCustomizer<MailPitContainer>> customizers) {

        MailPitContainer mailpit = ContainerFactory.createContainer(properties, MailPitContainer.class);
        return customizers.customize(mailpit);
    }

    @Bean
    @Order(0)
    ContainerCustomizer<MailPitContainer> mailPitContainerCustomizer(final MailPitProperties properties,
                                                                     final Optional<Network> network) {
        return mailpit -> {
            if (properties.isVerbose()) {
                mailpit.withEnv("MP_VERBOSE", "true");
            }

            mailpit.withEnv("MP_MAX_MESSAGES", Objects.toString(properties.getMaxMessages()));
            mailpit.waitingFor(Wait.forLogMessage(".*accessible via.*", 1));
            mailpit.withNetworkAliases(MAILPIT_NETWORK_ALIAS);

            network.ifPresent(mailpit::withNetwork);
        };
    }

    @Bean
    ContainerCustomizers<MailPitContainer, ContainerCustomizer<MailPitContainer>>
    mailPitContainerCustomizers(ObjectProvider<ContainerCustomizer<MailPitContainer>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }

    @Bean
    DynamicPropertyRegistrar mailPitContainerProperties(final MailPitConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.mailpit.host", connectionDetails::host);
            registry.add("container.mailpit.port-http", connectionDetails::portHttp);
            registry.add("container.mailpit.port-smtp", connectionDetails::portSMTP);
            registry.add("container.mailpit.server", connectionDetails::serverUrl);
        };
    }
}
