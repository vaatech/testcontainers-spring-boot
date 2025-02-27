package com.github.vaatech.testcontainers.autoconfigure.postgresql;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.ContainerFactory;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.postgresql.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;

@AutoConfiguration(
        before = ServiceConnectionAutoConfiguration.class,
        after = DockerPresenceAutoConfiguration.class)
@ConditionalOnPostgreSQLContainerEnabled
@ConditionalOnClass(PostgreSQLContainer.class)
@EnableConfigurationProperties(PostgreSQLProperties.class)
public class PostgreSQLContainerAutoConfiguration {

    private static final String POSTGRESQL_NETWORK_ALIAS = "postgresql.testcontainer.docker";

    @ServiceConnection
    @Bean(name = BEAN_NAME_CONTAINER_POSTGRESQL, destroyMethod = "stop")
    public PostgreSQLContainer<?>
    postgresql(PostgreSQLProperties properties,
               ContainerCustomizers<PostgreSQLContainer<?>, ContainerCustomizer<PostgreSQLContainer<?>>> customizers) {

        PostgreSQLContainer<?> postgresql = ContainerFactory.createContainer(properties, PostgreSQLContainer.class);
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

            network.ifPresent(postgresql::withNetwork);
        };
    }

    @Bean
    ContainerCustomizers<PostgreSQLContainer<?>, ContainerCustomizer<PostgreSQLContainer<?>>>
    postgreSQLContainerCustomizers(ObjectProvider<ContainerCustomizer<PostgreSQLContainer<?>>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }

    @Bean
    DynamicPropertyRegistrar postgreSQLContainerProperties(final JdbcConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.postgresql.url", connectionDetails::getJdbcUrl);
            registry.add("container.postgresql.username", connectionDetails::getUsername);
            registry.add("container.postgresql.password", connectionDetails::getPassword);
        };
    }
}