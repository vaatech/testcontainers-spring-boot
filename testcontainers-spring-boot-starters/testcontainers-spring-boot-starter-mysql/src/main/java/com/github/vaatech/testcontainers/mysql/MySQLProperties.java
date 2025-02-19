package com.github.vaatech.testcontainers.mysql;

import com.github.vaatech.testcontainers.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.mysql")
public class MySQLProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_MYSQL = "containerMySQL";
    public static final String DEFAULT_USERNAME = "test";
    public static final String DEFAULT_PASSWORD = "test";
    public static final String DEFAULT_DATABASE = "test_db";
    public static final String DEFAULT_HOST = "localhost";

    private String host = DEFAULT_HOST;
    private String database = DEFAULT_DATABASE;
    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private Integer[] exposedPorts = new Integer[]{3306};

    private String initScriptPath;
    private String encoding = "utf8mb4";
    private String collation = "utf8mb4_unicode_ci";

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create("mysql:8.0-debian");
    }
}
