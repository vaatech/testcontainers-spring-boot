package com.github.vaatech.testcontainers.mailpit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = ServiceConnectionAutoConfiguration.class)
@EnableConfigurationProperties(MailPitProperties.class)
public class MailPitConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    MailPitConnectionDetails mailPitConnectionDetails(final MailPitProperties properties) {
        return new MailPitConnectionDetails() {
            @Override
            public String host() {
                return properties.getHost();
            }

            @Override
            public int portHttp() {
                return properties.getPortHttp();
            }

            @Override
            public int portSMTP() {
                return properties.getPortSmtp();
            }

            @Override
            public String serverUrl() {
                return String.format("http://%s:%d", host(), portHttp());
            }
        };
    }
}
