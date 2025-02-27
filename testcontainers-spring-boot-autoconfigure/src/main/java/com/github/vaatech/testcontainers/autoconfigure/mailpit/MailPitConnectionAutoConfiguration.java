package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.vaatech.testcontainers.autoconfigure.mailpit.invoker.ApiClient;
import com.github.vaatech.testcontainers.autoconfigure.mailpit.rest.ApplicationApi;
import com.github.vaatech.testcontainers.autoconfigure.mailpit.rest.MessageApi;
import com.github.vaatech.testcontainers.autoconfigure.mailpit.rest.MessagesApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.test.context.DynamicPropertyRegistrar;

import java.net.http.HttpClient;
import java.time.Duration;

@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(MailPitProperties.class)
@Import(MailPitContainerConfiguration.class)
public class MailPitConnectionAutoConfiguration {

    @Bean
    DynamicPropertyRegistrar mailPitContainerProperties(final MailPitConnectionDetails connectionDetails) {
        return registry -> {
            registry.add("container.mailpit.host", connectionDetails::host);
            registry.add("container.mailpit.port-http", connectionDetails::portHttp);
            registry.add("container.mailpit.port-smtp", connectionDetails::portSMTP);
            registry.add("container.mailpit.server", connectionDetails::serverUrl);
        };
    }

    @Configuration(proxyBeanMethods = false)
    static class MailboxConfiguration {

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
}
