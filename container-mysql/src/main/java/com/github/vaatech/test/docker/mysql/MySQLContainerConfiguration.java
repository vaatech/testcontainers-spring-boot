package com.github.vaatech.test.docker.mysql;

import com.github.vaatech.test.docker.common.util.ContainerUtils;
import com.github.vaatech.test.docker.common.spring.DockerContainer;
import com.github.vaatech.test.docker.common.spring.DockerPresenceAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static com.github.vaatech.test.docker.mysql.MySQLProperties.BEAN_NAME_CONTAINER_MYSQL;
import static java.util.Collections.emptyList;

/**
 * {@link MySQLContainerConfiguration} is a configuration class that sets up and configures a MySQL
 * container using the Testcontainers library.
 *
 * <p>This class is used to conditionally register a {@link GenericContainer} bean for a MySQL
 * container, based on the presence of certain properties. The container is configured with
 * properties from a {@link MySQLProperties} class, and the container's connection details are added
 * to the application's {@link ConfigurableEnvironment}.
 *
 * <p>The class is annotated with {@code @ConditionalOnExpression} and
 * {@code @ConditionalOnProperty}, which means that the bean for the MySQL container will only be
 * registered if the 'containers.enabled' property is set to true and the 'container.mysql.enabled'
 * property is set. This allows for the configuration of the MySQL container to be easily turned on
 * or off.
 *
 * @see MySQLProperties
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${containers.enabled:true}")
@AutoConfigureAfter(DockerPresenceAutoConfiguration.class)
@ConditionalOnProperty(name = "container.mysql.enabled", matchIfMissing = true)
@EnableConfigurationProperties(MySQLProperties.class)
public class MySQLContainerConfiguration {

  private static final Logger LOGGER =
      LogManager.getFormatterLogger(MySQLContainerConfiguration.class);

  private final ConfigurableEnvironment environment;
  private final MySQLProperties properties;

  public MySQLContainerConfiguration(
      ConfigurableEnvironment environment, MySQLProperties properties) {
    this.environment = environment;
    this.properties = properties;
  }

  /**
   * Bean that starts and stops the MySQL container. The container is configured with properties
   * specified in {@link MySQLProperties}.
   *
   * @param environment The application's {@link ConfigurableEnvironment}
   * @param properties Properties for configuring the MySQL container
   * @return The started MySQL container
   */
  @SuppressWarnings("resource")
  @Bean(name = BEAN_NAME_CONTAINER_MYSQL, destroyMethod = "stop")
  public DockerContainer mysql(
      ObjectProvider<List<MySQLContainerCustomizer>> containerCustomizers) {

    MySQLContainer<?> mysql =
        new MySQLContainer<>(ContainerUtils.getDockerImageName(properties))
            .withExposedPorts(properties.getPort())
            .withUsername(properties.getUsername())
            .withPassword(properties.getPassword())
            .withDatabaseName(properties.getDatabase());

    Optional.ofNullable(containerCustomizers.getIfAvailable())
        .orElse(emptyList())
        .forEach(customizer -> customizer.customize(mysql));

    return mysql;
  }

  @Bean
  public MySQLContainerCustomizer mySQLContainerLogConsumer() {
    return (container) ->
        container.withLogConsumer(
            ContainerUtils.containerLogsConsumer(LogManager.getLogger("container-mysql")));
  }

  @Bean
  public MySQLContainerCustomizer mySQLContainerCreateDatabaseIfNotExists() {
    return (container) -> container.withUrlParam("createDatabaseIfNotExist", "true");
  }

  @Bean
  public MySQLContainerCustomizer mySQLContainerWaitStrategyCustomizer() {
    return (container) -> container.waitingFor(Wait.forHealthcheck());
  }

  /**
   * Registers the MySQL environment by adding the relevant properties to the {@link
   * ConfigurableEnvironment}.
   *
   * <p>This includes the mapped port, host, database, username, password, and connection options.
   *
   * <p>The JDBC connection URL is also logged for reference.
   *
   * @param mysql the {@link MySQLContainer} containing the MySQL server information
   * @param environment the {@link ConfigurableEnvironment} to add properties to
   * @param properties the {@link MySQLProperties} containing the configuration for the MySQL server
   */
  private void registerMySQLEnvironment(MySQLContainer<?> mysql) {

    Integer mappedPort = mysql.getMappedPort(properties.getPort());
    String host = mysql.getHost();
    String connOptions = mysql.getAdditionalUrlParams();
    String jdbcURL = mysql.getJdbcUrl();

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put("container.mysql.port", mappedPort);
    map.put("container.mysql.host", host);
    map.put("container.mysql.database", properties.getDatabase());
    map.put("container.mysql.username", properties.getUsername());
    map.put("container.mysql.password", properties.getPassword());
    map.put("container.mysql.options", connOptions);

    LOGGER.info(
        "Started mysql server. Connection details: %s, JDBC connection url: " + jdbcURL, map);
    MapPropertySource propertySource = new MapPropertySource("containerMySQLInfo", map);
    environment.getPropertySources().addFirst(propertySource);
  }
}
