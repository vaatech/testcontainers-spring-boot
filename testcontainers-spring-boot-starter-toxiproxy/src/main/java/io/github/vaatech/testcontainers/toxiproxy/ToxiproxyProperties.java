package io.github.vaatech.testcontainers.toxiproxy;

import io.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.toxiproxy")
public class ToxiproxyProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_TOXIPOROXY = "containerToxiproxy";

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create("ghcr.io/shopify/toxiproxy:2.6.0");
    }
}
