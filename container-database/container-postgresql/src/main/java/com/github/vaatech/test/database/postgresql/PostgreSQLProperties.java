package com.github.vaatech.test.database.postgresql;

import com.github.vaatech.test.database.common.CommonContainerDatabaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.postgresql")
public class PostgreSQLProperties extends CommonContainerDatabaseProperties {
  static final String BEAN_NAME_CONTAINER_POSTGRESQL = "containerPostgreSql";

  @Override
  public DockerImage getDefaultDockerImage() {
    return DockerImage.create("postgres:13-alpine");
  }
}
