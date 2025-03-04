package com.github.vaatech.testcontainers.keycloak;

import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import com.github.vaatech.testcontainers.core.ContainerImage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.keycloak")
public class KeycloakProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_KEYCLOAK = "containerKeycloak";

    public static final String DEFAULT_ADMIN_USER = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin";
    public static final String DEFAULT_REALM = "master";
    public static final String DEFAULT_CONTEXT_PATH = "";
    public static final String DEFAULT_DB_VENDOR = "dev-mem";

    private String host = "localhost";

    private Integer httpPort = 8080;

    /**
     * The admin username to use.
     */
    private String adminUser = DEFAULT_ADMIN_USER;
    /**
     * The Keycloak admin password to use.
     */
    private String adminPassword = DEFAULT_ADMIN_PASSWORD;
    /**
     * The relative auth URL of the container. Maybe needs to be tweaked for the WaitStrategy for different Keycloak versions (/auth vs. /auth/).
     */
    private String contextPath = DEFAULT_CONTEXT_PATH;
    /**
     * Classpath location of a JSON file to for importing resources into Keycloak. No prefix is needed.
     */
    private String importFile;
    /**
     * If this is empty then it tries to autodetected. Else it should be one of: h2, postgres, mysql, mariadb, oracle, mssql.
     */
    private String dbVendor = DEFAULT_DB_VENDOR;
    /**
     * Specify hostname of the database (optional).
     */
    private String dbAddr;
    /**
     * Specify port of the database (optional, default is DB vendor default port).
     */
    private String dbPort;
    /**
     * Specify name of the database to use (optional, default is keycloak).
     */
    private String dbDatabase;
    /**
     * Specify name of the schema to use for DB that support schemas (optional, default is public on Postgres).
     */
    private String dbSchema;
    /**
     * Specify user to use to authenticate to the database (optional, default is ``).
     */
    private String dbUser;
    /**
     * Specify user to authenticate to the database via file input (alternative to DB_USER).
     */
    private String dbUserFile;
    /**
     * Specify user's password to use to authenticate to the database (optional, default is ``).
     */
    private String dbPassword;
    /**
     * Specify user's password to use to authenticate to the database via file input (alternative to DB_PASSWORD).
     */
    private String dbPasswordFile;

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create(ContainerImage.KEYCLOAK.toString());
    }
}
