package io.github.vaatech.testcontainers.mysql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.Container;

import static org.assertj.core.api.Assertions.assertThat;

public class DisableMySQLTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MySQLContainerAutoConfiguration.class));

    @Test
    public void contextLoads() {
        contextRunner
                .withPropertyValues("container.mysql.enabled=false")
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Container.class)
                        .doesNotHaveBean("datasourceMySqlDependencyPostProcessor"));
    }

}
