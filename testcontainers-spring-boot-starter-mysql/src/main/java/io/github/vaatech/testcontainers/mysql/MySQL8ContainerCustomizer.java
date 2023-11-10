package io.github.vaatech.testcontainers.mysql;

import org.testcontainers.containers.MySQLContainer;

public class MySQL8ContainerCustomizer implements MySQLContainerCustomizer {
    private final MySQLProperties properties;

    public MySQL8ContainerCustomizer(MySQLProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(MySQLContainer<?> container) {
        container
                .withEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes")
                .withCommand("--character-set-server=" + properties.getEncoding(),
                        "--collation-server=" + properties.getCollation())
                .withInitScript(properties.getInitScriptPath());
    }
}
