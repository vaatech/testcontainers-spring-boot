package com.github.vaatech.testcontainers.mailpit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.vaatech.testcontainers.ConditionalOnContainersEnabled;
import com.github.vaatech.testcontainers.mailpit.rest.ApplicationRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessageRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessagesRestApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;

@AutoConfiguration
@ConditionalOnContainersEnabled
public class MailboxAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    HttpClient mailpitHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper mailboxObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationRestApi applicationRestApi(@MailPitHost String host,
                                          @MailPitPortHTTP int port,
                                          HttpClient httpClient,
                                          ObjectMapper mailboxObjectMapper) {
        return new ApplicationRestApi(httpClient, mailboxObjectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    MessageRestApi messageRestApi(@MailPitHost String host,
                                  @MailPitPortHTTP int port,
                                  HttpClient httpClient,
                                  ObjectMapper mailboxObjectMapper) {
        return new MessageRestApi(httpClient, mailboxObjectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    MessagesRestApi messagesRestApi(@MailPitHost String host,
                                    @MailPitPortHTTP int port,
                                    HttpClient httpClient,
                                    ObjectMapper mailboxObjectMapper) {
        return new MessagesRestApi(httpClient, mailboxObjectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    Mailbox mailbox(ApplicationRestApi applicationRestApi,
                    MessagesRestApi messagesRestApi,
                    MessageRestApi messageRestApi) {
        return new Mailbox(applicationRestApi, messagesRestApi, messageRestApi);
    }
}
