package com.github.vaatech.testcontainers.autoconfigure.mysql;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MySQLContainerAutoConfigurationTest {


    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DataSourceAutoConfiguration.class,
                    JdbcTemplateAutoConfiguration.class,
                    TestcontainersPropertySourceAutoConfiguration.class,
                    ServiceConnectionAutoConfiguration.class,
                    DockerPresenceAutoConfiguration.class,
                    TestcontainersEnvironmentAutoConfiguration.class,
                    MySQLContainerDependenciesAutoConfiguration.class,
                    MySQLContainerAutoConfiguration.class));

    @Test
    public void connectionDetailsAreAvailable() {
        contextRunner
                .run(context -> {
                    JdbcConnectionDetails connectionDetails = context.getBean(JdbcConnectionDetails.class);
                    assertThat(connectionDetails).isNotNull();
                });
    }

    @Test
    public void shouldConnectToMySQL() {
        contextRunner
                .withPropertyValues("container.mysql.docker-image=mysql:8.0.41")
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    MySQLProperties properties = context.getBean(MySQLProperties.class);

                    var versionString = jdbcTemplate.queryForObject("select version()", String.class);

                    var dockerImageVersion = properties.getDockerImage().getVersion();

                    Assertions.assertThat(versionString)
                            .as("The database version can be set using a container rule parameter")
                            .startsWith(dockerImageVersion);
                });
    }

    @Test
    public void shouldInitDBForMySQL() {
        contextRunner
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

                    jdbcTemplate.execute("""
                            CREATE TABLE person (
                                first_name VARCHAR(50) NOT NULL,
                                last_name VARCHAR(50) NOT NULL
                            );
                            """);

                    jdbcTemplate.execute("INSERT INTO person(first_name, last_name) values('Sam', 'Brannen');");

                    Integer count = jdbcTemplate.queryForObject("select count(first_name) from person where first_name = 'Sam' ", Integer.class);
                    Assertions.assertThat(count).isEqualTo(1);
                });
    }

    @Test
    public void shouldSetupDependsOnForAllDataSources() {
        contextRunner
                .run(context -> {
                    ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

                    String[] beanNamesForType =
                            BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, DataSource.class);

                    assertThat(beanNamesForType)
                            .as("Auto-configured datasource should be present")
                            .hasSize(1)
                            .contains("dataSource");

                    asList(beanNamesForType).forEach(beanName -> assertThat(beanFactory.getBeanDefinition(beanName).getDependsOn())
                            .isNotNull()
                            .isNotEmpty()
                            .contains(MySQLProperties.BEAN_NAME_CONTAINER_MYSQL));
                });
    }

    @Test
    void shouldHaveCustomizer() {
        contextRunner
                .withUserConfiguration(MySQLContainerAutoConfigurationTest.CustomizedMySQLContainerConfiguration.class)
                .run(context -> {
                    var type = ResolvableType.forType(new ParameterizedTypeReference<ContainerCustomizer<MySQLContainer<?>>>() {
                    });

                    String[] mySQLContainerCustomizerList = context.getBeanNamesForType(type);

                    MySQLContainer<?> containerMySQL = context.getBean(MySQLContainer.class);

                    assertThat(mySQLContainerCustomizerList).hasSizeGreaterThanOrEqualTo(2);
                    Assertions.assertThat(containerMySQL.getEnvMap().containsKey("CUSTOMIZED_ENV01")).isTrue();
                    Assertions.assertThat(containerMySQL.getEnvMap().containsKey("CUSTOMIZED_ENV02")).isTrue();
                });

    }

    @Test
    public void propertiesAreAvailable() {
        contextRunner
                .run(context -> {
                    var environment = context.getEnvironment();
                    assertThat(environment.getProperty("container.mysql.port")).isNotEmpty();
                    assertThat(environment.getProperty("container.mysql.host")).isNotEmpty();
                    assertThat(environment.getProperty("container.mysql.database")).isNotEmpty();
                    assertThat(environment.getProperty("container.mysql.username")).isNotEmpty();
                    assertThat(environment.getProperty("container.mysql.password")).isNotEmpty();
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomizedMySQLContainerConfiguration {

        @Bean
        @Order(1)
        public ContainerCustomizer<MySQLContainer<?>> mySQLContainerCustomizerEnv01() {
            return container -> container.withEnv("CUSTOMIZED_ENV01", "CUSTOMIZED_VALUE01");
        }

        @Bean
        @Order(2)
        public ContainerCustomizer<MySQLContainer<?>> mySQLContainerCustomizerEnv02() {
            return container -> container.withEnv("CUSTOMIZED_ENV02", "CUSTOMIZED_VALUE02");
        }
    }
}
