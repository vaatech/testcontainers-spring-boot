package com.github.vaatech.testcontainers.examples.keycloak.test.config;

import com.github.vaatech.testcontainers.autoconfigure.ContainerCustomizer;
import com.github.vaatech.testcontainers.autoconfigure.keycloak.ConditionalOnKeycloakContainerEnabled;
import com.github.vaatech.testcontainers.core.checks.AbstractInitOnStartupStrategy;
import com.github.vaatech.testcontainers.keycloak.KeycloakProperties;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import static com.github.vaatech.testcontainers.examples.keycloak.postgresql.BaseKeycloakPostgresSampleApplicationTests.*;
import static dasniko.testcontainers.keycloak.ExtendableKeycloakContainer.MASTER_REALM;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakWaitStrategyConfiguration {

    @Bean
    @Order(2)
    @ConditionalOnKeycloakContainerEnabled
    ContainerCustomizer<KeycloakContainer>
    keycloakContainerImportRealmContainerCustomizer(final KeycloakProperties properties) {
        return container -> container
                .withEnv("KC_CLI_PASSWORD", properties.getAdminPassword())
                .waitingFor(compositeWaitStrategy(properties));
    }

    private static WaitStrategy compositeWaitStrategy(final KeycloakProperties properties) {
        WaitAllStrategy strategy = new WaitAllStrategy();
        return strategy
                .withStrategy(Wait.forHttp(properties.getContextPath() + "/health/started").forPort(9000))
                .withStrategy(new AdminLoginCommand(properties))
                .withStrategy(new CreateRealmCommand())
                .withStrategy(new CreateClientCommand())
                .withStrategy(new CreateUserCommand())
                .withStrategy(new SetUserPasswordCommand())
                .withStartupTimeout(properties.getStartupTimeout());
    }

    static class AdminLoginCommand extends AbstractInitOnStartupStrategy {

        private final KeycloakProperties properties;

        AdminLoginCommand(KeycloakProperties properties) {
            this.properties = properties;
        }

        @Override
        public String[] getScriptToExecute() {
            return new String[]{
                    "/opt/keycloak/bin/kcadm.sh",
                    "config",
                    "credentials",
                    "--server", "http://localhost:8080",
                    "--realm", MASTER_REALM,
                    "--user", properties.getAdminUser()
            };
        }
    }

    static class CreateRealmCommand extends AbstractInitOnStartupStrategy {

        @Override
        public String[] getScriptToExecute() {
            return new String[]{
                    "/opt/keycloak/bin/kcadm.sh",
                    "create",
                    "realms",
                    "-s", "realm=%s".formatted(REALM),
                    "-s", "enabled=true"
            };
        }
    }

    static class CreateClientCommand extends AbstractInitOnStartupStrategy {

        @Override
        public String[] getScriptToExecute() {
            return new String[]{
                    "/opt/keycloak/bin/kcadm.sh",
                    "create",
                    "clients",
                    "-r", REALM,
                    "-s", "clientId=%s".formatted(CLIENT_ID),
                    "-s", "id=%s".formatted(CLIENT_UUID),
                    "-s", "enabled=true",
                    "-s", "clientAuthenticatorType=client-secret",
                    "-s", "secret=%s".formatted(CLIENT_SECRET),
            };
        }
    }

    static class CreateUserCommand extends AbstractInitOnStartupStrategy {

        @Override
        public String[] getScriptToExecute() {
            return new String[]{
                    "/opt/keycloak/bin/kcadm.sh",
                    "create",
                    "users",
                    "-r", REALM,
                    "-s", "username=%s".formatted(USERNAME),
                    "-s", "email=%s".formatted(EMAIL),
                    "-s", "enabled=true",
                    "-s", "firstName=%s".formatted("User"),
                    "-s", "lastName=%s".formatted("Test"),
            };
        }
    }

    static class SetUserPasswordCommand extends AbstractInitOnStartupStrategy {

        @Override
        public String[] getScriptToExecute() {
            return new String[]{
                    "/opt/keycloak/bin/kcadm.sh",
                    "set-password",
                    "-r", REALM,
                    "--username", USERNAME,
                    "--new-password", PASSWORD_PLAIN_TEXT,
            };
        }
    }
}
