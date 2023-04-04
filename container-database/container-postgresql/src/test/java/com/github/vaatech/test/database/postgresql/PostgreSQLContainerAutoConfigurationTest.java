package com.github.vaatech.test.database.postgresql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static com.github.vaatech.test.database.postgresql.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("enabled")
@SpringBootTest(
    classes = {PostgreSQLContainerAutoConfigurationTest.TestConfiguration.class},
    properties = {"container.postgresql.init-script-path=initScript.sql"})
class PostgreSQLContainerAutoConfigurationTest {

  @Autowired private ConfigurableListableBeanFactory beanFactory;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private ConfigurableEnvironment environment;

  @Test
  void shouldConnectToPostgreSQL() {
    assertThat(jdbcTemplate.queryForObject("select version()", String.class))
        .contains("PostgreSQL");
  }

  @Test
  void shouldSaveAndGetUnicode() {
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS employee(id INT, name VARCHAR(64));");
    jdbcTemplate.execute("insert into employee (id, name) values (1, 'some data \uD83D\uDE22');");

    assertThat(jdbcTemplate.queryForObject("select name from employee where id = 1", String.class))
        .isEqualTo("some data \uD83D\uDE22");
  }

  @Test
  public void shouldInitDBForPostgreSQL() throws Exception {
    assertThat(
            jdbcTemplate.queryForObject(
                "select count(first_name) from users where first_name = 'Sam' ", Integer.class))
        .isEqualTo(1);
  }

  @Test
  void propertiesAreAvailable() {
    assertThat(environment.getProperty("container.postgresql.port")).isNotEmpty();
    assertThat(environment.getProperty("container.postgresql.host")).isNotEmpty();
    assertThat(environment.getProperty("container.postgresql.database")).isNotEmpty();
    assertThat(environment.getProperty("container.postgresql.username")).isNotEmpty();
    assertThat(environment.getProperty("container.postgresql.password")).isNotEmpty();
    assertThat(environment.getProperty("container.postgresql.init-script-path")).isNotEmpty();
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

  private void hasDependsOn(String beanName) {
    assertThat(beanFactory.getBeanDefinition(beanName).getDependsOn())
        .isNotNull()
        .isNotEmpty()
        .contains(BEAN_NAME_CONTAINER_POSTGRESQL);
  }

  @Configuration
  @EnableAutoConfiguration
  static class TestConfiguration {}
}
