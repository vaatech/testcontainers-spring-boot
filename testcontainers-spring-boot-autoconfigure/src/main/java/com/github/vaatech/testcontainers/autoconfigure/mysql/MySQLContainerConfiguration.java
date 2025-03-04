package com.github.vaatech.testcontainers.autoconfigure.mysql;

import com.github.vaatech.testcontainers.autoconfigure.ContainerConfigurer;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.core.ContainerFactory;
import com.github.vaatech.testcontainers.mysql.MySQLProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.MySQLContainer;

import static com.github.vaatech.testcontainers.mysql.MySQLProperties.BEAN_NAME_CONTAINER_MYSQL;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MySQLContainer.class)
@ConditionalOnMissingBean(MySQLContainer.class)
@ConditionalOnMySQLContainerEnabled
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerConfiguration {

    private static final String MYSQL_NETWORK_ALIAS = "mysql.testcontainer.docker";

    @ServiceConnection
    @Bean(name = BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
    public MySQLContainer<?>
    mysql(final MySQLProperties properties,
          final ContainerFactory containerFactory,
          final ContainerConfigurer configurer,
          final ObjectProvider<ContainerCustomizer<MySQLContainer<?>>> customizers) {

        MySQLContainer<?> mysql = containerFactory.createContainer(properties, MySQLContainer.class);
        return configurer.configure(mysql, properties, customizers.orderedStream());
    }

    @Bean
    @Order(0)
    ContainerCustomizer<MySQLContainer<?>> standartMySQLContainerCustomizer(final MySQLProperties properties) {
        return container -> {
            container.withUsername(properties.getUsername());
            container.withPassword(properties.getPassword());
            container.withDatabaseName(properties.getDatabase());
            container.withNetworkAliases(MYSQL_NETWORK_ALIAS);
            container.withExposedPorts(properties.getExposedPorts());
            container.withCommand(
                    "--character-set-server=" + properties.getEncoding(),
                    "--collation-server=" + properties.getCollation()
            );
        };
    }
}
