package com.github.vaatech.test.database.mysql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static com.github.vaatech.test.common.spring.DockerEnvironmentAutoConfiguration.DOCKER_ENVIRONMENT;
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

    @Autowired ConfigurableListableBeanFactory beanFactory;

    @Autowired ConfigurableEnvironment environment;

    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired MySQLProperties properties;

    @Test
    public void shouldConnectToMySQL() throws Exception {
      var versionString = jdbcTemplate.queryForObject("select version()", String.class);

      var dockerImageVersion =
          DockerImageName.parse(properties.getDockerImage().fullImageName()).getVersionPart();

      assertThat(versionString)
          .as("The database version can be set using a container rule parameter")
          .startsWith(dockerImageVersion);
    }

    @Sql(
        statements =
            """
                    CREATE TABLE person (
                        first_name VARCHAR(50) NOT NULL,
                        last_name VARCHAR(50) NOT NULL
                    );
                    INSERT INTO person(first_name, last_name) values('Sam', 'Brannen');
                    """)
    @Test
    public void shouldInitDBForMySQL() throws Exception {
      assertThat(
              jdbcTemplate.queryForObject(
                  "select count(first_name) from person where first_name = 'Sam' ", Integer.class))
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
          .contains(DOCKER_ENVIRONMENT, MySQLProperties.BEAN_NAME_CONTAINER_MYSQL);
    }
  }

  @TestPropertySource(properties = {"container.mysql.docker-image.name=mysql:5.7.34"})
  @Nested
  @DisplayName("AutoConfigured Datasource with mysql:5.7.34")
  class Mysql5Image extends DefaultTests {}

  @Configuration
  @EnableAutoConfiguration
  static class TestConfiguration {}
}
