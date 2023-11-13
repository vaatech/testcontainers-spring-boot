package io.github.vaatech.testcontainers.artemis;

import jakarta.jms.*;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ArtemisContainerAutoConfigurationTest {

    @SpringBootTest(
            classes = {ArtemisContainerAutoConfigurationTest.TestConfiguration.class},
            properties = {
                    "spring.profiles.active=enabled",
            })
    @DisplayName("Default Credentials Test")
    @Nested
    class DefaultCredentialsTest {

        @Autowired
        ArtemisContainer container;

        @Test
        void shouldStartArtemisWithDefaultCredentials() {
            assertThat(container.getUser()).isEqualTo(ArtemisProperties.DEFAULT_USERNAME);
            assertThat(container.getPassword()).isEqualTo(ArtemisProperties.DEFAULT_PASSWORD);
            assertFunctionality(container, false);
        }
    }

    @SpringBootTest(
            classes = {ArtemisContainerAutoConfigurationTest.TestConfiguration.class},
            properties = {
                    "spring.profiles.active=enabled",
                    "container.artemis.username=custom-username",
                    "container.artemis.password=custom-password"
            })
    @DisplayName("Custom Credentials Test")
    @Nested
    class CustomCredentialsTest {

        @Autowired
        ArtemisContainer container;

        @Test
        void shouldStartArtemisWithCustomCredentials() {
            assertThat(container.getUser()).isEqualTo("custom-username");
            assertThat(container.getPassword()).isEqualTo("custom-password");
            assertFunctionality(container, false);
        }
    }

    @SpringBootTest(
            classes = {ArtemisContainerAutoConfigurationTest.TestConfiguration.class},
            properties = {
                    "spring.profiles.active=enabled",
                    "container.artemis.allow-anonymous-login=true"
            })
    @DisplayName("Anonymous Login Test")
    @Nested
    class AnonymousLoginTest {

        @Autowired
        ArtemisContainer container;

        @Test
        void shouldStartArtemisWithAnonymousLogin() {
            assertThat(container.getUser()).isEqualTo(ArtemisProperties.DEFAULT_USERNAME);
            assertThat(container.getPassword()).isEqualTo(ArtemisProperties.DEFAULT_PASSWORD);
            assertFunctionality(container, true);
        }
    }


    @SneakyThrows
    private void assertFunctionality(ArtemisContainer artemis, boolean anonymousLogin) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(artemis.getBrokerUrl());
        if (!anonymousLogin) {
            connectionFactory.setUser(artemis.getUser());
            connectionFactory.setPassword(artemis.getPassword());
        }
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("test-queue");
        MessageProducer producer = session.createProducer(destination);

        String contentMessage = "Testcontainers";
        TextMessage message = session.createTextMessage(contentMessage);
        producer.send(message);

        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage messageReceived = (TextMessage) consumer.receive();
        assertThat(messageReceived.getText()).isEqualTo(contentMessage);
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {
    }

}