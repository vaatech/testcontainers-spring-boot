package com.github.vaatech.test.database.mysql;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.vaatech.test.common.spring.DockerContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.LinkedHashMap;

@Slf4j
public class MySQLContainer<SELF extends MySQLContainer<SELF>> extends JdbcDatabaseContainer<SELF>
    implements DockerContainer {
  static final String DEFAULT_USER = "test";

  static final String DEFAULT_PASSWORD = "test";

  public static final Integer MYSQL_PORT = 3306;

  private String username = DEFAULT_USER;

  private String password = DEFAULT_PASSWORD;

  private static final String MYSQL_ROOT_USER = "root";

  private String databaseName = "test";

  private ConfigurableEnvironment environment;

  public MySQLContainer(DockerImageName dockerImageName) {
    super(dockerImageName);

    addExposedPort(MYSQL_PORT);
  }

  @Override
  protected void configure() {
    addEnv("DB_ROOT_PASSWORD", MYSQL_ROOT_USER);
    addEnv("MYSQL_DATABASE", databaseName);
    addEnv("MYSQL_USER", username);
    addEnv("MYSQL_PASSWORD", password);

    setStartupAttempts(3);
  }

  @Override
  public String getDriverClassName() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      return "com.mysql.cj.jdbc.Driver";
    } catch (ClassNotFoundException e) {
      return "com.mysql.jdbc.Driver";
    }
  }

  @Override
  public String getJdbcUrl() {
    String additionalUrlParams = constructUrlParameters("?", "&");
    return "jdbc:mysql://"
        + getHost()
        + ":"
        + getMappedPort(MYSQL_PORT)
        + "/"
        + databaseName
        + additionalUrlParams;
  }

  public String getAdditionalUrlParams() {
    return constructUrlParameters("?", "&");
  }

  @Override
  protected String constructUrlForConnection(String queryString) {
    String url = super.constructUrlForConnection(queryString);

    if (!url.contains("useSSL=")) {
      String separator = url.contains("?") ? "&" : "?";
      url = url + separator + "useSSL=false";
    }

    if (!url.contains("allowPublicKeyRetrieval=")) {
      url = url + "&allowPublicKeyRetrieval=true";
    }

    if (!url.contains("createDatabaseIfNotExist=")) {
      url = url + "&createDatabaseIfNotExist=true";
    }

    return url;
  }

  @Override
  protected void containerIsStarted(InspectContainerResponse containerInfo) {
    super.containerIsStarted(containerInfo);

    if (environment == null) {
      log.debug("Environment is not provided. Skipping registration.");
      return;
    }

    Integer mappedPort = getMappedPort(MYSQL_PORT);
    String host = getHost();
    String connOptions = getAdditionalUrlParams();
    String jdbcURL = getJdbcUrl();

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put("container.mysql.port", mappedPort);
    map.put("container.mysql.host", host);
    map.put("container.mysql.database", getDatabaseName());
    map.put("container.mysql.username", getUsername());
    map.put("container.mysql.password", getPassword());
    map.put("container.mysql.options", connOptions);

    log.info("Started mysql server. Connection details: %s, JDBC connection url: " + jdbcURL, map);
    MapPropertySource propertySource = new MapPropertySource("containerMySQLInfo", map);
    environment.getPropertySources().addFirst(propertySource);

    logInfo();
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getTestQueryString() {
    return "SELECT 1";
  }

  @Override
  public SELF withDatabaseName(final String databaseName) {
    this.databaseName = databaseName;
    return self();
  }

  @Override
  public SELF withUsername(final String username) {
    this.username = username;
    return self();
  }

  @Override
  public SELF withPassword(final String password) {
    this.password = password;
    return self();
  }

  @Override
  public void setEnvironment(ConfigurableEnvironment environment) {
    this.environment = environment;
  }

  @Override
  public Logger log() {
    return log;
  }

  @Override
  public GenericContainer<?> unwrap() {
    return null;
  }

  @Override
  public String name() {
    return "MySQL";
  }
}
