package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import com.github.vaatech.testcontainers.examples.keycloak.test.config.ClientRedirectUriConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.ContainersConfiguration;

@SpringBootTest(
        classes = {
                KeycloakPostgresSampleApplication.class,
                ContainersConfiguration.class,
                ClientRedirectUriConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "container.keycloak.mount-volumes[0].type=CLASSPATH",
        "container.keycloak.mount-volumes[0].path=/test-realm.json",
        "container.keycloak.mount-volumes[0].container-path=/opt/keycloak/data/import/test-realm.json",
})
class KeycloakPropertiesMountVolumeConfigurationTest extends BaseKeycloakPostgresSampleApplicationTests {
}
