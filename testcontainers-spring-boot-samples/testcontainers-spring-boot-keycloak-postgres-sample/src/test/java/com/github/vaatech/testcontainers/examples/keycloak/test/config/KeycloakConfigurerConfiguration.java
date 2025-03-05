package com.github.vaatech.testcontainers.examples.keycloak.test.config;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.*;
import static org.assertj.core.api.Assertions.assertThat;

@Configuration
public class KeycloakConfigurerConfiguration {

    @Bean
    KeycloakConfigurer configurer(final Keycloak keycloak) {
        return new KeycloakConfigurer(keycloak);
    }

    static class KeycloakConfigurer implements InitializingBean, ApplicationListener<ServletWebServerInitializedEvent> {

        private final Keycloak keycloak;

        private ClientResource clientResource;

        KeycloakConfigurer(Keycloak keycloak) {
            this.keycloak = keycloak;
        }

        @Override
        public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
            int port = event.getWebServer().getPort();
            ClientRepresentation representation = clientResource.toRepresentation();
            representation.setRedirectUris(List.of("http://localhost:%s/login/oauth2/code/test".formatted(port)));
            clientResource.update(representation);
        }

        @Override
        public void afterPropertiesSet() throws Exception {

            var realm = new RealmRepresentation();
            realm.setRealm(REALM);
            realm.setVerifyEmail(false);
            realm.setEnabled(true);
            realm.setSslRequired("external");

            keycloak.realms().create(realm);

            realm = keycloak.realm(REALM).toRepresentation();
            assertThat(realm).isNotNull();

            var client = new ClientRepresentation();
            client.setId(CLIENT_UUID);
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

            try (Response response = keycloak.realm(REALM).clients().create(client)) {
                final String clientUUID = CreatedResponseUtil.getCreatedId(response);
                clientResource = keycloak.realm(REALM).clients().get(clientUUID);
                assertThat(clientResource).isNotNull();
            }

            UsersResource usersResource = keycloak.realm(REALM).users();

            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setTemporary(false);
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(PASSWORD_PLAIN_TEXT);

            UserRepresentation userTest = new UserRepresentation();
            userTest.setUsername(USERNAME);
            userTest.setFirstName("User");
            userTest.setLastName("Test");
            userTest.setCreatedTimestamp(Instant.now().toEpochMilli());
            userTest.setEmail(EMAIL);
            userTest.setEmailVerified(true);
            userTest.setEnabled(true);
            userTest.setRequiredActions(List.of());
            userTest.setCredentials(List.of(credentials));

            try (Response response = usersResource.create(userTest)) {
                CreatedResponseUtil.getCreatedId(response);
            }
        }
    }
}
