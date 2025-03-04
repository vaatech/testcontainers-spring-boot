package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import com.github.vaatech.testcontainers.mailpit.Mailbox;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig
@TestPropertySource(properties = {
        "spring.mail.host=${container.mailpit.host:localhost}",
        "spring.mail.port=${container.mailpit.port-smtp:1025}"
})
class MailPitContainerSendEmailTest {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Mailbox mailbox;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired(required = false)
    MailPitConnectionDetails connectionDetails;

    @Test
    void propertiesAreAvailable() {
        assertThat(environment.getProperty("container.mailpit.port-smtp")).isNotEmpty();
        assertThat(environment.getProperty("container.mailpit.port-http")).isNotEmpty();
        assertThat(environment.getProperty("container.mailpit.host")).isNotEmpty();
    }

    @Test
    void shouldSendEmail() {
        assertThat(this.connectionDetails).isNotNull();

        var mailMessage = buildMail();

        mailSender.send(mailMessage);

        var maybeMail = mailbox.findFirst("spring-boot");
        assertThat(maybeMail.isPresent()).isTrue();

        var mail = maybeMail.get();
        assertThat(mail.getSubject()).isEqualTo(mailMessage.getSubject());
    }

    private SimpleMailMessage buildMail() {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("Subject");
        mailMessage.setFrom("no-reply@testcontainers-spring-boot.com");
        mailMessage.setTo("customer@testcontainers-spring-boot.com");
        mailMessage.setText("Lorem ipsum dolor sit amet");
        return mailMessage;
    }

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration({
            MailSenderAutoConfiguration.class,
            TestcontainersPropertySourceAutoConfiguration.class,
            ServiceConnectionAutoConfiguration.class,
            DockerPresenceAutoConfiguration.class,
            TestcontainersEnvironmentAutoConfiguration.class,
            MailPitConnectionAutoConfiguration.class,
            MailPitConnectionDetailsAutoConfiguration.class
    })
    static class TestConfiguration {

    }
}
