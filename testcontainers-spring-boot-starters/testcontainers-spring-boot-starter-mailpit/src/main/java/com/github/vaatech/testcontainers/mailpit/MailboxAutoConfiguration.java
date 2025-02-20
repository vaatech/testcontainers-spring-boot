package com.github.vaatech.testcontainers.mailpit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.vaatech.testcontainers.ConditionalOnContainersEnabled;
import com.github.vaatech.testcontainers.mailpit.invoker.ApiClient;
import com.github.vaatech.testcontainers.mailpit.rest.ApplicationApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessageApi;
import com.github.vaatech.testcontainers.mailpit.rest.MessagesApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

@AutoConfiguration
@ConditionalOnContainersEnabled
public class MailboxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ObjectMapper mailboxObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    ApiClient apiClient(final ObjectMapper mailboxObjectMapper,
                        final MailPitConnectionDetails connectionDetails) {

        final HttpClient.Builder httpClient = HttpClient.newBuilder();
        final ApiClient client = new ApiClient();
        client.setHttpClientBuilder(httpClient);
        client.setObjectMapper(mailboxObjectMapper);
        client.updateBaseUri(connectionDetails.serverUrl());
        client.setConnectTimeout(Duration.ofSeconds(5));
        client.setReadTimeout(Duration.ofSeconds(30));
        return client;
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationApi applicationRestApi(final ApiClient apiClient) {
        return new ApplicationApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    MessageApi messageRestApi(final ApiClient apiClient) {
        return new MessageApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    MessagesApi messagesRestApi(final ApiClient apiClient) {
        return new MessagesApi(apiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    Mailbox mailbox(ApplicationApi applicationRestApi,
                    MessagesApi messagesRestApi,
                    MessageApi messageRestApi) {
        return new Mailbox(applicationRestApi, messagesRestApi, messageRestApi);
    }
}
