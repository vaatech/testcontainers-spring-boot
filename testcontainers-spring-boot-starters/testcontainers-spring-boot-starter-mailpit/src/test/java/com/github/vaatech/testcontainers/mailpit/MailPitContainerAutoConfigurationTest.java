package com.github.vaatech.testcontainers.mailpit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = MailPitContainerAutoConfigurationTest.TestConfiguration.class,
        properties = {}
)
class MailPitContainerAutoConfigurationTest {

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Mailbox mailbox;

    @Test
    void propertiesAreAvailable() {
        assertThat(environment.getProperty("container.mailpit.port-smtp")).isNotEmpty();
        assertThat(environment.getProperty("container.mailpit.port-http")).isNotEmpty();
        assertThat(environment.getProperty("container.mailpit.host")).isNotEmpty();
    }

    @Test
    void shouldSendEmail() {
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

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }

}
