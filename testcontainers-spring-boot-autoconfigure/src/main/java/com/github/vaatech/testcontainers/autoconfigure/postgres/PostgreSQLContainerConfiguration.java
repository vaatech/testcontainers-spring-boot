package com.github.vaatech.testcontainers.autoconfigure.postgres;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.postgres.PostgreSQLProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static com.github.vaatech.testcontainers.postgres.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PostgreSQLContainer.class)
@ConditionalOnPostgreSQLContainerEnabled
@EnableConfigurationProperties(PostgreSQLProperties.class)
public class PostgreSQLContainerConfiguration {

    public static final String POSTGRESQL_NETWORK_ALIAS = "postgresql.testcontainer.docker";

    @ServiceConnection
    @Bean(name = BEAN_NAME_CONTAINER_POSTGRESQL, destroyMethod = "stop")
    public PostgreSQLContainer<?>
    postgresql(final PostgreSQLProperties properties,
               final ContainerFactory containerFactory,
               final ContainerCustomizers<PostgreSQLContainer<?>, ContainerCustomizer<PostgreSQLContainer<?>>> customizers) {

        PostgreSQLContainer<?> postgresql = containerFactory.createContainer(properties, PostgreSQLContainer.class);
        return customizers.customize(postgresql);
    }

    @Bean
    @Order(0)
    public ContainerCustomizer<PostgreSQLContainer<?>>
    standardPostgreSQLContainerCustomizer(final PostgreSQLProperties properties,
                                          final Optional<Network> network) {
        return postgresql -> {
            postgresql.withUsername(properties.getUser());
            postgresql.withPassword(properties.getPassword());
            postgresql.withDatabaseName(properties.getDatabase());
            postgresql.withNetworkAliases(POSTGRESQL_NETWORK_ALIAS);
            postgresql.withExtraHost(DEFAULT_DNS_NAME, "host-gateway");
            network.ifPresent(postgresql::withNetwork);
        };
    }

    @Bean
    ContainerCustomizers<PostgreSQLContainer<?>, ContainerCustomizer<PostgreSQLContainer<?>>>
    postgreSQLContainerCustomizers(ObjectProvider<ContainerCustomizer<PostgreSQLContainer<?>>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }
}
