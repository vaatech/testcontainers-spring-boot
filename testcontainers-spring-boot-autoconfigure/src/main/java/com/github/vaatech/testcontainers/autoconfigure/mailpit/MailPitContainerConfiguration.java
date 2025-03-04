package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.autoconfigure.ContainerConfigurer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.mailpit.MailPitContainer;
import com.github.vaatech.testcontainers.mailpit.MailPitProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Objects;

import static com.github.vaatech.testcontainers.mailpit.MailPitProperties.BEAN_NAME_CONTAINER_MAILPIT;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(MailPitContainer.class)
@ConditionalOnMailPitContainerEnabled
@EnableConfigurationProperties(MailPitProperties.class)
public class MailPitContainerConfiguration {

    public static final String MAILPIT_NETWORK_ALIAS = "mailpit.testcontainer.docker";

    @ServiceConnection(type = MailPitConnectionDetails.class)
    @Bean(name = BEAN_NAME_CONTAINER_MAILPIT, destroyMethod = "stop")
    public MailPitContainer
    mailpit(final MailPitProperties properties,
            final ContainerFactory containerFactory,
            final ContainerConfigurer configurer,
            final ObjectProvider<ContainerCustomizer<MailPitContainer>> customizers) {

        MailPitContainer mailpit = containerFactory.createContainer(properties, MailPitContainer.class);
        return configurer.configure(mailpit, properties, customizers.orderedStream());
    }

    @Bean
    @Order(0)
    ContainerCustomizer<MailPitContainer> mailPitContainerCustomizer(final MailPitProperties properties) {
        return mailpit -> {
            if (properties.isVerbose()) {
                mailpit.withEnv("MP_VERBOSE", "true");
            }

            mailpit.withEnv("MP_MAX_MESSAGES", Objects.toString(properties.getMaxMessages()));
            mailpit.waitingFor(Wait.forLogMessage(".*accessible via.*", 1));
            mailpit.withNetworkAliases(MAILPIT_NETWORK_ALIAS);
        };
    }
}
