package com.github.vaatech.testcontainers.examples.keycloak.mysql;


import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static dasniko.testcontainers.keycloak.ExtendableKeycloakContainer.MASTER_REALM;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                KeycloakMySQLSampleApplication.class,
                KeycloakMySQLSampleApplicationTest.TestConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "container.keycloak.attach-container-log=true",
        "container.mysql.attach-container-log=true",
        "container.mysql.mount-volumes[0].container-path=/docker-entrypoint-initdb.d",
        "container.mysql.mount-volumes[0].path=/com/github/vaatech/testcontainers/examples/keycloak/mysql",
        "container.mysql.mount-volumes[0].type=CLASSPATH",
})
class KeycloakMySQLSampleApplicationTest {

    @Autowired
    KeycloakContainer container;

    @Autowired
    MySQLContainer<?> mysql;

    @Autowired
    Keycloak keycloak;

    @Test
    void test() {
        var dependencies = container.getDependencies();
        assertThat(dependencies).hasSize(1);
        assertThat(dependencies).contains(mysql);

        var info = keycloak.realm(MASTER_REALM).toRepresentation();
        assertThat(info).isNotNull();
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfiguration {

        @Bean
        @Order(1)
        ContainerCustomizer<KeycloakContainer>
        keycloakContainerContainerCustomizer(final MySQLContainer<?> mysql,
                                             final JdbcConnectionDetails connectionDetails) {
            return container -> {
                container.dependsOn(mysql);
                container.withEnv("KC_DB", "mysql");
                container.withEnv("KC_DB_URL", connectionDetails.getJdbcUrl().replace("localhost", DEFAULT_DNS_NAME));
                container.withEnv("KC_DB_USERNAME", connectionDetails.getUsername());
                container.withEnv("KC_DB_PASSWORD", connectionDetails.getPassword());
                container.withEnv("KC_DB_SCHEMA", mysql.getDatabaseName());
            };
        }
    }

}