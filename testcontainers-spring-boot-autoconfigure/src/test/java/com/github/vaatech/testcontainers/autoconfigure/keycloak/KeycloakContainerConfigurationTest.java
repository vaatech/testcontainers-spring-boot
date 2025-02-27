package com.github.vaatech.testcontainers.autoconfigure.keycloak;

import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static dasniko.testcontainers.keycloak.ExtendableKeycloakContainer.MASTER_REALM;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
public class KeycloakContainerConfigurationTest {

    @Autowired
    Keycloak keycloak;

    @Test
    void test() {
        var info = keycloak.realm(MASTER_REALM).toRepresentation();
        assertThat(info).isNotNull();
    }

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration({
            TestcontainersPropertySourceAutoConfiguration.class,
            ServiceConnectionAutoConfiguration.class,
            DockerPresenceAutoConfiguration.class,
            TestcontainersEnvironmentAutoConfiguration.class,
            KeycloakConnectionAutoConfiguration.class,
            KeycloakConnectionDetailsAutoConfiguration.class
    })
    static class TestConfiguration {

    }
}
