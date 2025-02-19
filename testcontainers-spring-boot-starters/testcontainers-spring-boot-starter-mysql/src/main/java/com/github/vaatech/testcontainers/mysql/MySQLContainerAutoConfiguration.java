package com.github.vaatech.testcontainers.mysql;

import com.github.vaatech.testcontainers.ContainerCustomizer;
import com.github.vaatech.testcontainers.ContainerCustomizers;
import com.github.vaatech.testcontainers.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.GenericContainerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

import java.util.Optional;

@AutoConfiguration(
        before = ServiceConnectionAutoConfiguration.class,
        after = DockerPresenceAutoConfiguration.class)
@ConditionalOnMySQLContainerEnabled
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerAutoConfiguration {

    @ServiceConnection
    @Bean(name = MySQLProperties.BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
    public MySQLContainer<?> mysql(MySQLProperties properties,
                                   ContainerCustomizers<MySQLContainer<?>, ContainerCustomizer<MySQLContainer<?>>> customizers) {

        MySQLContainer<?> mysql = GenericContainerFactory.getGenericContainer(
                properties,
                new ParameterizedTypeReference<>() {
                }
        );

        return customizers.customize(mysql);
    }

    @Bean
    ContainerCustomizers<MySQLContainer<?>, ContainerCustomizer<MySQLContainer<?>>>
    mySQLContainerCustomizers(ObjectProvider<ContainerCustomizer<MySQLContainer<?>>> customizers) {
        return new ContainerCustomizers<>(customizers);
    }

    @Bean
    @Order(0)
    ContainerCustomizer<MySQLContainer<?>> standartMySQLContainerCustomizer(final MySQLProperties properties,
                                                                            final Optional<Network> network) {
        return container -> {
            container
                    .withUsername(properties.getUsername())
                    .withPassword(properties.getPassword())
                    .withDatabaseName(properties.getDatabase())
                    .withExposedPorts(properties.getExposedPorts());

            network.ifPresent(container::withNetwork);
        };
    }

    @Bean
    DynamicPropertyRegistrar mySQLContainerProperties(final MySQLContainer<?> container) {
        return registry -> {
            registry.add("container.mysql.port", () -> container.getMappedPort(MySQLContainer.MYSQL_PORT));
            registry.add("container.mysql.host", container::getHost);
            registry.add("container.mysql.database", container::getDatabaseName);
            registry.add("container.mysql.username", container::getUsername);
            registry.add("container.mysql.password", container::getPassword);
        };
    }
}
