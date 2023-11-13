package io.github.vaatech.testcontainers.artemis;

import io.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.artemis")
public class ArtemisProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_ARTEMIS = "containerArtemis";
    public static final String DEFAULT_USERNAME = "artemis";
    public static final String DEFAULT_PASSWORD = "artemis";

    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private boolean allowAnonymousLogin = false;

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create("apache/activemq-artemis:2.31.0-alpine");
    }
}
