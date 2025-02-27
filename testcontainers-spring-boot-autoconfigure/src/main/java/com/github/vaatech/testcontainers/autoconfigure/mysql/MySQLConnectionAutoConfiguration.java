package com.github.vaatech.testcontainers.autoconfigure.mysql;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistrar;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(MySQLProperties.class)
@Import(MySQLContainerConfiguration.class)
public class MySQLConnectionAutoConfiguration {

    @Bean
    DynamicPropertyRegistrar mySQLContainerProperties(final JdbcConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.mysql.url", connectionDetails::getJdbcUrl);
            registry.add("container.mysql.username", connectionDetails::getUsername);
            registry.add("container.mysql.password", connectionDetails::getPassword);
        };
    }
}
