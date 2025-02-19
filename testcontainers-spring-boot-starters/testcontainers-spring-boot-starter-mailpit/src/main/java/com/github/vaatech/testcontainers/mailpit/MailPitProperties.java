package com.github.vaatech.testcontainers.mailpit;

import com.github.vaatech.testcontainers.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("container.mailpit")
public class MailPitProperties extends CommonContainerProperties {

    public static final String BEAN_NAME_CONTAINER_MAILPIT = "containerMailpit";
    public static final Integer MAILPIT_HTTP_PORT = 8025;
    public static final Integer MAILPIT_SMTP_PORT = 1025;

    private String host = "localhost";
    private boolean verbose;
    private Integer portHttp = MAILPIT_HTTP_PORT;
    private Integer portSmtp = MAILPIT_SMTP_PORT;
    private int maxMessages = 500;

    @Override
    public DockerImage getDefaultDockerImage() {
        return DockerImage.create("axllent/mailpit:v1.22.3");
    }
}
