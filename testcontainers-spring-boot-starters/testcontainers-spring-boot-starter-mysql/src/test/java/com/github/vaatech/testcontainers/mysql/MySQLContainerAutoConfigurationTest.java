package com.github.vaatech.testcontainers.mysql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.List;

import static com.github.vaatech.testcontainers.TestcontainersEnvironmentAutoConfiguration.TESTCONTAINERS_ENVIRONMENT;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MySQLContainerAutoConfigurationTest {

    @SpringBootTest(
            classes = {MySQLContainerAutoConfigurationTest.TestConfiguration.class},
            properties = {
                    "spring.profiles.active=enabled",
                    "container.mysql.docker-image.name=mysql:8.0.32"
            })
    @DisplayName("Default AutoConfigured Datasource")
    @Nested
    class DefaultTests {

        @Autowired
        ConfigurableListableBeanFactory beanFactory;

        @Autowired
        ConfigurableEnvironment environment;

        @Autowired
        JdbcTemplate jdbcTemplate;

        @Autowired(required = false)
        MySQLProperties properties;

        @Test
        public void shouldConnectToMySQL() throws Exception {
            var versionString = jdbcTemplate.queryForObject("select version()", String.class);

            var dockerImageVersion =
                    DockerImageName.parse(properties.getDockerImage().fullImageName()).getVersionPart();

            assertThat(versionString)
                    .as("The database version can be set using a container rule parameter")
                    .startsWith(dockerImageVersion);
        }

        @Sql(statements = """
                CREATE TABLE person (
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL
                );
                INSERT INTO person(first_name, last_name) values('Sam', 'Brannen');
                """)
        @Test
        public void shouldInitDBForMySQL() throws Exception {
            assertThat(jdbcTemplate.queryForObject("select count(first_name) from person where first_name = 'Sam' ", Integer.class))
                    .isEqualTo(1);
        }

        @Test
        public void shouldSetupDependsOnForAllDataSources() throws Exception {
            String[] beanNamesForType =
                    BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, DataSource.class);
            assertThat(beanNamesForType)
                    .as("Auto-configured datasource should be present")
                    .hasSize(1)
                    .contains("dataSource");
            asList(beanNamesForType).forEach(this::hasDependsOn);
        }

        @Test
        public void propertiesAreAvailable() {
            assertThat(environment.getProperty("container.mysql.port")).isNotEmpty();
            assertThat(environment.getProperty("container.mysql.host")).isNotEmpty();
            assertThat(environment.getProperty("container.mysql.database")).isNotEmpty();
            assertThat(environment.getProperty("container.mysql.username")).isNotEmpty();
            assertThat(environment.getProperty("container.mysql.password")).isNotEmpty();
        }

        private void hasDependsOn(String beanName) {
            assertThat(beanFactory.getBeanDefinition(beanName).getDependsOn())
                    .isNotNull()
                    .isNotEmpty()
                    .contains(TESTCONTAINERS_ENVIRONMENT, MySQLProperties.BEAN_NAME_CONTAINER_MYSQL);
        }
    }

    @TestPropertySource(properties = {"container.mysql.docker-image.name=mysql:5.7.34"})
    @Nested
    @DisplayName("AutoConfigured Datasource with mysql:5.7.34")
    class MySQL5Image extends DefaultTests {
    }

    @Nested
    @DisplayName("AutoConfigured Datasource with Customizers")
    @Import(TestConfigurationWithCustomizedMySQLContainer.class)
    @SpringBootTest(
            classes = {MySQLContainerAutoConfigurationTest.TestConfiguration.class},
            properties = {"spring.profiles.active=enabled"})
    class MySQLCustomizerTest {

        @Autowired
        List<MySQLContainerCustomizer> mySQLContainerCustomizerList;

        @Autowired
        MySQLContainer<?> containerMySQL;

        @Test
        void shouldHaveCustomizer() {
            assertThat(mySQLContainerCustomizerList).hasSizeGreaterThanOrEqualTo(2);
            assertThat(containerMySQL.getEnvMap().containsKey("CUSTOMIZED_ENV01")).isTrue();
            assertThat(containerMySQL.getEnvMap().containsKey("CUSTOMIZED_ENV02")).isTrue();
        }
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfigurationWithCustomizedMySQLContainer {
        @Bean
        public MySQLContainerCustomizer mySQLContainerCustomizerEnv01() {
            return container -> container.withEnv("CUSTOMIZED_ENV01", "CUSTOMIZED_VALUE01");
        }

        @Bean
        public MySQLContainerCustomizer mySQLContainerCustomizerEnv02() {
            return container -> container.withEnv("CUSTOMIZED_ENV02", "CUSTOMIZED_VALUE02");
        }
    }
}
