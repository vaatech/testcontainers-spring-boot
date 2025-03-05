package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.keycloak.ConditionalOnKeycloakContainerEnabled;
import com.github.vaatech.testcontainers.autoconfigure.keycloak.KeycloakConnectionDetails;
import com.github.vaatech.testcontainers.autoconfigure.postgres.ConditionalOnPostgreSQLContainerEnabled;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static org.assertj.core.api.Assertions.assertThat;


public abstract class BaseKeycloakPostgresSampleApplicationTests {

    public static final String REALM = "test-realm";
    public static final String CLIENT_UUID = "9b60057a-9bb1-44dd-8851-2794de9369ab";
    public static final String CLIENT_ID = "any-client";
    public static final String CLIENT_SECRET = "08f64721-7fef-4d8b-a0fc-8f940a621451";
    public static final String USERNAME = "user.test_9090";
    public static final String EMAIL = "user.test_9090@invalid";
    public static final String PASSWORD_PLAIN_TEXT = "changeMe2024!!";

    @Autowired(required = false)
    KeycloakContainer container;

    @Autowired(required = false)
    PostgreSQLContainer<?> psql;

    @LocalServerPort
    private int port;

    @Test
    void test() throws IOException {
        assertThat(container).isNotNull();
        assertThat(psql).isNotNull();

        var dependencies = container.getDependencies();
        assertThat(dependencies).hasSize(1);
        assertThat(dependencies).contains(psql);

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage dexLoginPage = webClient.getPage("http://localhost:%s/".formatted(port));
            dexLoginPage.<HtmlInput>getElementByName("username").type(USERNAME);
            dexLoginPage.<HtmlInput>getElementByName("password").type(PASSWORD_PLAIN_TEXT);

            DomElement loginButton = dexLoginPage.getElementById("kc-login");
            HtmlPage appPage = loginButton.click();
            assertThat(appPage.getElementById("name").getTextContent()).isEqualTo(USERNAME);
            assertThat(appPage.getElementById("email").getTextContent()).isEqualTo(EMAIL);
            assertThat(appPage.getElementById("subject").getTextContent()).isNotBlank();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ContainersConfiguration {

        @Bean
        @Order(1)
        @ConditionalOnPostgreSQLContainerEnabled
        @ConditionalOnKeycloakContainerEnabled
        ContainerCustomizer<KeycloakContainer>
        keycloakContainerContainerCustomizer(final PostgreSQLContainer<?> psql,
                                             final JdbcConnectionDetails connectionDetails) {
            return container -> {
                container.dependsOn(psql);
                container.withEnv("KC_DB", "postgres");
                container.withEnv("KC_DB_URL", connectionDetails.getJdbcUrl().replace("localhost", DEFAULT_DNS_NAME));
                container.withEnv("KC_DB_USERNAME", connectionDetails.getUsername());
                container.withEnv("KC_DB_PASSWORD", connectionDetails.getPassword());
                container.withEnv("KC_DB_SCHEMA", "public");
            };
        }

        @Bean
        @Order(1)
        @ConditionalOnPostgreSQLContainerEnabled
        ContainerCustomizer<PostgreSQLContainer<?>> postgreSQLContainerContainerCustomizer() {
            return container -> { //
                container.setTmpFsMapping(Map.of("/var/lib/postgresql/data", "rw"));
            };
        }

        @Bean
        DynamicPropertyRegistrar oauth2ClientProperties(final KeycloakConnectionDetails connectionDetails) {
            return registry -> {
                Supplier<Object> issuerUriSupplier = () -> { //
                    return "%s/realms/%s".formatted(connectionDetails.authServerUrl(), REALM);
                };
                registry.add("spring.security.oauth2.client.registration.test.client-id", () -> CLIENT_ID);
                registry.add("spring.security.oauth2.client.registration.test.client-secret", () -> CLIENT_SECRET);
                registry.add("spring.security.oauth2.client.registration.test.scope", () -> "openid,email,profile");
                registry.add("spring.security.oauth2.client.provider.test.issuer-uri", issuerUriSupplier);
            };
        }
    }
}
