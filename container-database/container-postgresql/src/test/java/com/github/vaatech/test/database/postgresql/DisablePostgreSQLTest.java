package com.github.vaatech.test.database.postgresql;

import com.github.vaatech.test.common.spring.DockerContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DisablePostgreSQLTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  PostgreSQLContainerAutoConfiguration.class,
                  PostgreSQLContainerDependenciesAutoConfiguration.class));

  @Test
  public void contextLoads() {
    contextRunner
        .withPropertyValues("container.postgresql.enabled=false")
        .run(
            (context) ->
                assertThat(context)
                    .hasNotFailed()
                    .doesNotHaveBean(DockerContainer.class)
                    .doesNotHaveBean("datasourcePostgreSqlDependencyPostProcessor"));
  }
}
