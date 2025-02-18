package com.github.vaatech.testcontainers.wiremock;

import com.github.vaatech.testcontainers.properties.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.wiremock")
public class WireMockProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_WIREMOCK = "containerWireMock";

    private boolean verbose;
    private String host = "localhost";
    private Integer port = 8080;
    private String baseUrl = "http://localhost:8080";

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create("wiremock/wiremock:3.7.0");
    }
}
