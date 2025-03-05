package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import com.github.vaatech.testcontainers.examples.keycloak.test.config.ClientRedirectUriConfiguration;
import com.github.vaatech.testcontainers.examples.keycloak.test.config.KeycloakWaitStrategyConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.ContainersConfiguration;

@SpringBootTest(
        classes = {
                KeycloakPostgresSampleApplication.class,
                ContainersConfiguration.class,
                KeycloakWaitStrategyConfiguration.class,
                ClientRedirectUriConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "container.keycloak.attach-container-log=true",
        "logging.level.com.github.vaatech.testcontainers.core.checks=DEBUG"
})
class KeycloakAdminCliConfigurationTest extends BaseKeycloakPostgresSampleApplicationTests {
}