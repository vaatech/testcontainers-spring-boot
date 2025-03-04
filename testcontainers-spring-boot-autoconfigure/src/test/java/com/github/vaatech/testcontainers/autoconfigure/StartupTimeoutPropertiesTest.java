package com.github.vaatech.testcontainers.autoconfigure;

import com.github.vaatech.testcontainers.core.config.CommonContainerProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "container.test.startup-timeout=120"
})
class StartupTimeoutPropertiesTest {

    @Autowired
    private TimeoutProperties timeoutProperties;

    @Test
    void testConnectionTimeout() {
        assertThat(timeoutProperties.getStartupTimeout())
                .isEqualTo(Duration.ofMillis(120000))
                .isEqualTo(Duration.ofSeconds(120));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(TimeoutProperties.class)
    static class TestConfiguration {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ConfigurationProperties("container.test")
    static class TimeoutProperties extends CommonContainerProperties {

        @Override
        public DockerImage getDefaultDockerImage() {
            return null;
        }
    }
}