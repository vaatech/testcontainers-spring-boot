package com.github.vaatech.testcontainers.autoconfigure.postgresql;

import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.github.vaatech.testcontainers.autoconfigure.postgresql.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class PostgreSQLContainerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DataSourceAutoConfiguration.class,
                    JdbcTemplateAutoConfiguration.class,
                    TestcontainersPropertySourceAutoConfiguration.class,
                    ServiceConnectionAutoConfiguration.class,
                    DockerPresenceAutoConfiguration.class,
                    TestcontainersEnvironmentAutoConfiguration.class,
                    PostgreSQLContainerAutoConfiguration.class,
                    PostgreSQLContainerDependenciesAutoConfiguration.class));

    @Test
    public void connectionDetailsAreAvailable() {
        contextRunner
                .run(context -> {
                    JdbcConnectionDetails connectionDetails = context.getBean(JdbcConnectionDetails.class);
                    assertThat(connectionDetails).isNotNull();
                });
    }

    @Test
    void shouldConnectToPostgreSQL() {
        contextRunner
                .withPropertyValues("container.postgresql.docker-image=postgres:16.7")
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    PostgreSQLProperties properties = context.getBean(PostgreSQLProperties.class);

                    var versionString = jdbcTemplate.queryForObject("select version()", String.class);

                    var dockerImageVersion = properties.getDockerImage().getVersion();
                    var dockerImageName = properties.getDockerImage().getName();

                    Assertions.assertThat(versionString)
                            .as("The database version can be set using a container rule parameter")
                            .containsIgnoringCase(dockerImageName)
                            .containsIgnoringCase(dockerImageVersion);
                });
    }

    @Test
    void shouldSaveAndGetUnicode() {
        contextRunner
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS employee(id INT, name VARCHAR(64));");
                    jdbcTemplate.execute("insert into employee (id, name) values (1, 'some data \uD83D\uDE22');");

                    assertThat(jdbcTemplate.queryForObject("select name from employee where id = 1", String.class)).isEqualTo("some data \uD83D\uDE22");
                });
    }

    @Test
    public void shouldInitDBForPostgreSQL() {
        contextRunner
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

                    jdbcTemplate.execute("""
                            CREATE TABLE person (
                                first_name VARCHAR(50) NOT NULL,
                                last_name VARCHAR(50) NOT NULL
                            );
                            """);

                    jdbcTemplate.execute("INSERT INTO person(first_name, last_name) values('Qnko', 'Sakyzov');");

                    Integer count = jdbcTemplate.queryForObject("select count(first_name) from person where first_name = 'Qnko' ", Integer.class);
                    Assertions.assertThat(count).isEqualTo(1);
                });
    }

    @Test
    public void propertiesAreAvailable() {
        contextRunner
                .run(context -> {
                    var environment = context.getEnvironment();
                    assertThat(environment.getProperty("container.postgresql.url")).isNotEmpty();
                    assertThat(environment.getProperty("container.postgresql.username")).isNotEmpty();
                    assertThat(environment.getProperty("container.postgresql.password")).isNotEmpty();
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
                            .contains(BEAN_NAME_CONTAINER_POSTGRESQL));
                });
    }

}