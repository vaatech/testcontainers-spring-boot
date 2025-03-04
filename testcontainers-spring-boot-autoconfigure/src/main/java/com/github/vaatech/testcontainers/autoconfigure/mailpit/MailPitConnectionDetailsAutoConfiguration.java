package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.mailpit.MailPitProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(MailPitProperties.class)
@EnableConfigurationProperties(MailPitProperties.class)
public class MailPitConnectionDetailsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MailPitConnectionDetails.class)
    MailPitConnectionDetails mailPitConnectionDetails(final MailPitProperties properties) {
        return new PropertiesMailPitConnectionDetails(properties);
    }
}
