package com.github.vaatech.test.database.mysql;

import com.github.vaatech.test.common.spring.ContainerCustomizer;
import com.github.vaatech.test.common.spring.CustomizableContainer;
import com.github.vaatech.test.common.spring.DockerContainer;
import com.github.vaatech.test.common.spring.DockerPresenceAutoConfiguration;
import com.github.vaatech.test.common.util.ContainerUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;

import java.util.List;
import java.util.Optional;

import static com.github.vaatech.test.database.mysql.MySQLProperties.BEAN_NAME_CONTAINER_MYSQL;
import static java.util.Collections.emptyList;

@AutoConfiguration(after = {DockerPresenceAutoConfiguration.class})
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnProperty(name = "container.mysql.enabled", matchIfMissing = true)
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerAutoConfiguration implements CustomizableContainer<MySQLContainer<?>> {

  private List<ContainerCustomizer<MySQLContainer<?>>> containerCustomizers = emptyList();

  @SuppressWarnings("resource")
  @Bean(name = BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
  @ConditionalOnMissingBean(name = BEAN_NAME_CONTAINER_MYSQL)
  public DockerContainer mysql(MySQLProperties properties, Optional<Network> network) {

    MySQLContainer<?> mysql =
        new MySQLContainer<>(ContainerUtils.getDockerImageName(properties))
            .withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")
            .withUsername(properties.getUsername())
            .withPassword(properties.getPassword())
            .withDatabaseName(properties.getDatabase())
            .withExposedPorts(properties.getExposedPorts())
            .withCommand(
                "--character-set-server=" + properties.getEncoding(),
                "--collation-server=" + properties.getCollation())
            .withLogConsumer(
                ContainerUtils.containerLogsConsumer(LogManager.getLogger("container-mysql")))
            .withInitScript(properties.getInitScriptPath());

    network.ifPresent(mysql::withNetwork);

    containerCustomizers.forEach(customizer -> customizer.customize(mysql));

    return new MySQLContainerDecorator(mysql);
  }

  @Override
  @Autowired(required = false)
  public void setCustomizers(List<ContainerCustomizer<MySQLContainer<?>>> containerCustomizers) {
    this.containerCustomizers = containerCustomizers;
  }
}
