package io.github.vaatech.testcontainers.properties.jdbc;

import io.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CommonJdbcContainerProperties extends CommonContainerProperties {

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
}
