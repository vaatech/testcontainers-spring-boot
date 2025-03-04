package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.keycloak.KeycloakConnectionDetails;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration.DEFAULT_DNS_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                KeycloakPostgresSampleApplication.class,
                KeycloakPostgresSampleApplicationTests.TestConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "container.postgresql.attach-container-log=true",
        "container.keycloak.attach-container-log=true",
})
class KeycloakPostgresSampleApplicationTests {

    static final String REALM = "test-realm";
    static final String CLIENT_ID = "any-client";
    static final String CLIENT_SECRET = "08f64721-7fef-4d8b-a0fc-8f940a621451";

    @Autowired
    KeycloakContainer container;

    @Autowired
    PostgreSQLContainer<?> psql;

    @Autowired
    Keycloak keycloak;

    @LocalServerPort
    private int port;

    @Test
    void test() throws IOException {
        var dependencies = container.getDependencies();
        assertThat(dependencies).hasSize(1);
        assertThat(dependencies).contains(psql);

        var realm = keycloak.realm(REALM).toRepresentation();
        assertThat(realm).isNotNull();

        var client = new ClientRepresentation();
        client.setClientId(CLIENT_ID);
        client.setBaseUrl("");
        client.setEnabled(true);
        client.setClientAuthenticatorType("client-secret");
        client.setSecret(CLIENT_SECRET);
        client.setProtocol("openid-connect");
        client.setBearerOnly(false);
        client.setConsentRequired(false);
        client.setStandardFlowEnabled(true);
        client.setImplicitFlowEnabled(false);
        client.setDirectAccessGrantsEnabled(true);
        client.setAuthorizationServicesEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setPublicClient(Boolean.FALSE);
        client.setRedirectUris(List.of("http://localhost:%s/login/oauth2/code/test".formatted(port)));

        try (Response response = keycloak.realm(REALM).clients().create(client)) {
            CreatedResponseUtil.getCreatedId(response);
        }

        UsersResource usersResource = keycloak.realm(REALM).users();

        final String passwordPlainText = "changeMe2024!!";
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(passwordPlainText);

        final String username = "user.test_9090";
        final String email = "user.test_9090@invalid";
        UserRepresentation userAdmin = new UserRepresentation();
        userAdmin.setUsername(username);
        userAdmin.setFirstName("User");
        userAdmin.setLastName("Test");
        userAdmin.setCreatedTimestamp(Instant.now().toEpochMilli());
        userAdmin.setEmail(email);
        userAdmin.setEmailVerified(true);
        userAdmin.setEnabled(true);
        userAdmin.setRequiredActions(List.of());
        userAdmin.setCredentials(List.of(credentials));

        try (Response response = usersResource.create(userAdmin)) {
            CreatedResponseUtil.getCreatedId(response);
        }

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setRedirectEnabled(true);
            HtmlPage dexLoginPage = webClient.getPage("http://localhost:%s/".formatted(port));
            dexLoginPage.<HtmlInput>getElementByName("username").type(username);
            dexLoginPage.<HtmlInput>getElementByName("password").type(passwordPlainText);

            DomElement loginButton = dexLoginPage.getElementById("kc-login");
            HtmlPage appPage = loginButton.click();
            assertThat(appPage.getElementById("name").getTextContent()).isEqualTo(username);
            assertThat(appPage.getElementById("email").getTextContent()).isEqualTo(email);
            assertThat(appPage.getElementById("subject").getTextContent()).isNotBlank();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfiguration {

        @Bean
        @Order(1)
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
                container.withRealmImportFile("/test-realm.json");
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
