package com.github.vaatech.testcontainers.autoconfigure.mysql;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizers;
import com.github.vaatech.testcontainers.autoconfigure.ContainerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

import java.util.Optional;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MySQLContainer.class)
@ConditionalOnMissingBean(MySQLContainer.class)
@ConditionalOnMySQLContainerEnabled
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerConfiguration {

    private static final String MYSQL_NETWORK_ALIAS = "mysql.testcontainer.docker";

    @ServiceConnection
    @Bean(name = MySQLProperties.BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
    public MySQLContainer<?>
    mysql(MySQLProperties properties,
          ContainerCustomizers<MySQLContainer<?>, ContainerCustomizer<MySQLContainer<?>>> customizers) {

        MySQLContainer<?> mysql = ContainerFactory.createContainer(properties, MySQLContainer.class);
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
                    .withNetworkAliases(MYSQL_NETWORK_ALIAS)
                    .withExposedPorts(properties.getExposedPorts())
                    .withExtraHost(DEFAULT_DNS_NAME, "host-gateway")
                    .withCommand(
                            "--character-set-server=" + properties.getEncoding(),
                            "--collation-server=" + properties.getCollation());

            network.ifPresent(container::withNetwork);
        };
    }
}
