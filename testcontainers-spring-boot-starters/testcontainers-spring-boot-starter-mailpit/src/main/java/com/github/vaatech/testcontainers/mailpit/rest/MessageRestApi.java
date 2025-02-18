package com.github.vaatech.testcontainers.mailpit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaatech.testcontainers.mailpit.model.Message;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MessageRestApi {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public MessageRestApi(HttpClient httpClient,
                          ObjectMapper objectMapper,
                          String host,
                          int port) {

        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = "http://" + host + ":" + port;
    }

    /**
     * Get message summary
     * Returns the summary of a message, marking the message as read.
     *
     * @param messageId Database ID (required)
     * @return Message
     */
    public Message message(String messageId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/message/" + messageId))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), Message.class);
            } else {
                throw new RuntimeException("Request failed with status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving message", e);
        }
    }
}
