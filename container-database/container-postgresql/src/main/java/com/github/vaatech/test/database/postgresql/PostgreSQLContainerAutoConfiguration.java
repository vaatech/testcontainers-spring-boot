package com.github.vaatech.test.database.postgresql;

import com.github.vaatech.test.common.spring.ContainerCustomizer;
import com.github.vaatech.test.common.spring.CustomizableContainer;
import com.github.vaatech.test.common.spring.DockerContainer;
import com.github.vaatech.test.common.spring.DockerPresenceAutoConfiguration;
import com.github.vaatech.test.common.util.ContainerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static com.github.vaatech.test.database.postgresql.PostgreSQLProperties.BEAN_NAME_CONTAINER_POSTGRESQL;
import static java.util.Collections.emptyList;

@Slf4j
@ConditionalOnExpression("${containers.enabled:true}")
@AutoConfiguration(after = {DockerPresenceAutoConfiguration.class})
@ConditionalOnProperty(name = "container.postgresql.enabled", matchIfMissing = true)
@EnableConfigurationProperties(PostgreSQLProperties.class)
public class PostgreSQLContainerAutoConfiguration
    implements CustomizableContainer<PostgreSQLContainer<?>> {
  private List<ContainerCustomizer<PostgreSQLContainer<?>>> containerCustomizers = emptyList();

  @SuppressWarnings("resource")
  @Bean(name = BEAN_NAME_CONTAINER_POSTGRESQL, destroyMethod = "stop")
  public DockerContainer postgresql(PostgreSQLProperties properties, Optional<Network> network) {

    PostgreSQLContainer<?> postgresql =
        new PostgreSQLContainer<>(ContainerUtils.getDockerImageName(properties))
            .withUsername(properties.getUsername())
            .withPassword(properties.getPassword())
            .withDatabaseName(properties.getDatabase())
            .withInitScript(properties.getInitScriptPath());

    network.ifPresent(postgresql::withNetwork);

    containerCustomizers.forEach(customizer -> customizer.customize(postgresql));

    return new PostgreSQLContainerDecorator(postgresql);
  }

  @Override
  public void setCustomizers(
      List<ContainerCustomizer<PostgreSQLContainer<?>>> containerCustomizers) {
    this.containerCustomizers = containerCustomizers;
  }
}
