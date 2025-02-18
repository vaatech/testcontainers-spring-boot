package com.github.vaatech.testcontainers.mailpit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaatech.testcontainers.ConditionalOnContainersEnabled;
import com.github.vaatech.testcontainers.mailpit.rest.ApplicationRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessageRestApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessagesRestApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationRestApi applicationRestApi(@MailPitHost String host,
                                          @MailPitPortHTTP int port,
                                          HttpClient httpClient,
                                          ObjectMapper objectMapper) {
        return new ApplicationRestApi(httpClient, objectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    MessageRestApi messageRestApi(@MailPitHost String host,
                                  @MailPitPortHTTP int port,
                                  HttpClient httpClient,
                                  ObjectMapper objectMapper) {
        return new MessageRestApi(httpClient, objectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    MessagesRestApi messagesRestApi(@MailPitHost String host,
                                    @MailPitPortHTTP int port,
                                    HttpClient httpClient,
                                    ObjectMapper objectMapper) {
        return new MessagesRestApi(httpClient, objectMapper, host, port);
    }

    @Bean
    @ConditionalOnMissingBean
    Mailbox mailbox(ApplicationRestApi applicationRestApi,
                    MessagesRestApi messagesRestApi,
                    MessageRestApi messageRestApi) {
        return new Mailbox(applicationRestApi, messagesRestApi, messageRestApi);
    }
}
