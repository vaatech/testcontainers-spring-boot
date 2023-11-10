package io.github.vaatech.testcontainers.mysql;

import io.github.vaatech.testcontainers.ContainerCustomizers;
import io.github.vaatech.testcontainers.DependsOnPostProcessor;
import io.github.vaatech.testcontainers.util.ContainerUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

import static io.github.vaatech.testcontainers.mysql.MySQLProperties.BEAN_NAME_CONTAINER_MYSQL;

@AutoConfiguration
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.mysql.enabled", matchIfMissing = true)
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    public static class MySQLContainerDependenciesConfiguration {

        @Bean
        public static BeanFactoryPostProcessor datasourceMySqlDependencyPostProcessor() {
            return new DependsOnPostProcessor(DataSource.class, BEAN_NAME_CONTAINER_MYSQL);
        }
    }

    @Bean(name = MySQLProperties.BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
    public MySQLContainer<?> mysql(MySQLProperties properties,
                                   MySQLContainerCustomizers customizers) {

        MySQLContainer<?> mysql = new MySQLContainer<>(ContainerUtils.getDockerImageName(properties));
        return customizers.customize(mysql);
    }

    @Bean
    MySQLContainerCustomizers mySQLContainerCustomizers(ObjectProvider<MySQLContainerCustomizer> customizers) {
        return new MySQLContainerCustomizers(customizers);
    }

    @Bean
    StandartMySQLContainerCustomizer standartMySQLContainerCustomizer(MySQLProperties properties,
                                                                      DynamicPropertyRegistry registry) {
        return new StandartMySQLContainerCustomizer(properties, registry);
    }

    static public class MySQLContainerCustomizers extends ContainerCustomizers<MySQLContainer<?>, MySQLContainerCustomizer> {
        public MySQLContainerCustomizers(ObjectProvider<? extends MySQLContainerCustomizer> customizers) {
            super(customizers);
        }
    }

    static final class StandartMySQLContainerCustomizer implements MySQLContainerCustomizer, Ordered {

        private final MySQLProperties properties;
        private final DynamicPropertyRegistry registry;

        StandartMySQLContainerCustomizer(MySQLProperties properties,
                                         DynamicPropertyRegistry registry) {
            this.properties = properties;
            this.registry = registry;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void customize(MySQLContainer<?> container) {

            container
                    .withUsername(properties.getUsername())
                    .withPassword(properties.getPassword())
                    .withDatabaseName(properties.getDatabase())
                    .withExposedPorts(properties.getExposedPorts())
                    .withLogConsumer(ContainerUtils.containerLogsConsumer(LoggerFactory.getLogger("container-mysql")));

            registry.add("container.mysql.port", () -> container.getMappedPort(MySQLContainer.MYSQL_PORT));
            registry.add("container.mysql.host", container::getHost);
            registry.add("container.mysql.database", container::getDatabaseName);
            registry.add("container.mysql.username", container::getUsername);
            registry.add("container.mysql.password", container::getPassword);
        }
    }
}
