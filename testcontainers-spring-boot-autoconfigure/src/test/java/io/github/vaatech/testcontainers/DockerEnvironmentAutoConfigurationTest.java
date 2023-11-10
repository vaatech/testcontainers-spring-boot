package io.github.vaatech.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootTest(
        classes = DockerEnvironmentAutoConfigurationTest.TestConfiguration.class,
        properties = {
//                "containers.enabled=false"
        }
)
public class DockerEnvironmentAutoConfigurationTest {

    @Autowired
    ConfigurableListableBeanFactory beanFactory;

    @Autowired
    ConfigurableEnvironment environment;

    @Test
    void test() {
        int a = 1;
    }

    @EnableAutoConfiguration
    @Configuration
    @ComponentScan(basePackages = "io.github.vaatech.testcontainers")
    static class TestConfiguration {
    }
}
