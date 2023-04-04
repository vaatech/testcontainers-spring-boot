package com.github.vaatech.test.database.mysql;

import com.github.vaatech.test.database.common.CommonContainerDatabaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.mysql")
public class MySQLProperties extends CommonContainerDatabaseProperties {
  public static final String BEAN_NAME_CONTAINER_MYSQL = "containerMySQL";

  String encoding = "utf8mb4";
  String collation = "utf8mb4_unicode_ci";
  /**
   * The SQL file path to execute after the container starts to initialize the database.
   */
  String initScriptPath;

  @Override
  public DockerImage getDefaultDockerImage() {
    return new DockerImage("", "mysql", "8.0-debian");
  }
}
