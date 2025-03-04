package com.github.vaatech.testcontainers.postgres;

import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import com.github.vaatech.testcontainers.core.ContainerImage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.testcontainers.containers.PostgreSQLContainer;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.postgresql")
public class PostgreSQLProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_POSTGRESQL = "containerPostgreSQL";

    private String host = "localhost";
    private int port = PostgreSQLContainer.POSTGRESQL_PORT;
    private String database = "test_db";
    private String user = "postgresql";
    private String password = "letmein";

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create(ContainerImage.POSTGRESQL.toString());
    }
}
