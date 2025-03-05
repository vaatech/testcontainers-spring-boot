package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import com.github.vaatech.testcontainers.examples.keycloak.test.config.KeycloakConfigurerConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.ContainersConfiguration;

@SpringBootTest(
        classes = {
                KeycloakPostgresSampleApplication.class,
                ContainersConfiguration.class,
                KeycloakConfigurerConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KeycloakProgrammaticConfigurationTest extends BaseKeycloakPostgresSampleApplicationTests {
}