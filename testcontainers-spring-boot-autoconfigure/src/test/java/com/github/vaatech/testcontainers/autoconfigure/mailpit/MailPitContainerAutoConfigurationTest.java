package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.autoconfigure.DockerPresenceAutoConfiguration;
import com.github.vaatech.testcontainers.autoconfigure.TestcontainersEnvironmentAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testcontainers.properties.TestcontainersPropertySourceAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.testcontainers.containers.Container;

import static org.assertj.core.api.Assertions.assertThat;


class MailPitContainerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    MailSenderAutoConfiguration.class,
                    TestcontainersPropertySourceAutoConfiguration.class,
                    ServiceConnectionAutoConfiguration.class,
                    DockerPresenceAutoConfiguration.class,
                    TestcontainersEnvironmentAutoConfiguration.class,
                    MailPitContainerAutoConfiguration.class,
                    MailPitConnectionDetailsAutoConfiguration.class,
                    MailboxAutoConfiguration.class));

    @Test
    void contextLoads() {
        contextRunner
                .withPropertyValues(
                        "container.mailpit.enabled=false"
                )
                .run((context) -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(Container.class));
    }

    @Test
    void propertiesAreAvailable() {
        contextRunner
                .run(context -> {
                    ConfigurableEnvironment environment = context.getEnvironment();
                    assertThat(environment).isNotNull();

                    assertThat(environment.getProperty("container.mailpit.port-smtp")).isNotEmpty();
                    assertThat(environment.getProperty("container.mailpit.port-http")).isNotEmpty();
                    assertThat(environment.getProperty("container.mailpit.host")).isNotEmpty();
                });
    }

    @Test
    void shouldSendEmail() {
        contextRunner
                .withPropertyValues(
                        "spring.mail.host=${container.mailpit.host:localhost}",
                        "spring.mail.port=${container.mailpit.port-smtp:1025}"
                )
                .run(context -> {
                    JavaMailSender mailSender = context.getBean(JavaMailSender.class);
                    Mailbox mailbox = context.getBean(Mailbox.class);

                    var mailMessage = buildMail();

                    mailSender.send(mailMessage);

                    var maybeMail = mailbox.findFirst("spring-boot");
                    assertThat(maybeMail.isPresent()).isTrue();

                    var mail = maybeMail.get();
                    assertThat(mail.getSubject()).isEqualTo(mailMessage.getSubject());
                });
    }

    @Test
    void shouldHaveDefaultConnectionDetails() {
        contextRunner
                .withPropertyValues(
                        "containers.enabled=false"
                )
                .run(context -> {
                    var connectionDetailsProvider = context.getBeanProvider(MailPitConnectionDetails.class);
                    assertThat(connectionDetailsProvider).isNotNull();

                    var connectionDetails = connectionDetailsProvider.getIfAvailable();
                    assertThat(connectionDetails).isNotNull();

                    var wireMockProperties = context.getBean(MailPitProperties.class);

                    assertThat(wireMockProperties).isNotNull();

                    assertThat(wireMockProperties.getPortHttp()).isEqualTo(connectionDetails.portHttp());
                    assertThat(wireMockProperties.getPortSmtp()).isEqualTo(connectionDetails.portSMTP());
                    assertThat(wireMockProperties.getHost()).isEqualTo(connectionDetails.host());
                });
    }

    private SimpleMailMessage buildMail() {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("Subject");
        mailMessage.setFrom("no-reply@testcontainers-spring-boot.com");
        mailMessage.setTo("customer@testcontainers-spring-boot.com");
        mailMessage.setText("Lorem ipsum dolor sit amet");
        return mailMessage;
    }
}
