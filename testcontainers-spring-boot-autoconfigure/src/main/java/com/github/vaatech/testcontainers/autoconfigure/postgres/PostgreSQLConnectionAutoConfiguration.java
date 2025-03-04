package com.github.vaatech.testcontainers.autoconfigure.postgres;

import com.github.vaatech.testcontainers.postgres.PostgreSQLProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistrar;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(PostgreSQLProperties.class)
@EnableConfigurationProperties(PostgreSQLProperties.class)
@Import(PostgreSQLContainerConfiguration.class)
public class PostgreSQLConnectionAutoConfiguration {

    @Bean
    DynamicPropertyRegistrar postgreSQLContainerProperties(final JdbcConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.postgresql.url", connectionDetails::getJdbcUrl);
            registry.add("container.postgresql.username", connectionDetails::getUsername);
            registry.add("container.postgresql.password", connectionDetails::getPassword);
        };
    }
}