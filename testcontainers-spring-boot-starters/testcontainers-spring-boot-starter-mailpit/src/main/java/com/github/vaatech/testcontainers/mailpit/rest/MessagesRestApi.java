package com.github.vaatech.testcontainers.mailpit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaatech.testcontainers.mailpit.model.DeleteRequest;
import com.github.vaatech.testcontainers.mailpit.model.MessagesSummary;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Optional;

@RequiredArgsConstructor
public class MessagesRestApi {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public MessagesRestApi(HttpClient httpClient,
                           ObjectMapper objectMapper,
                           String host,
                           int port) {

        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = "http://" + host + ":" + port;
    }

    /**
     * Search messages
     * Returns the latest messages matching a search.
     *
     * @param query Search query (required)
     * @param start Pagination offset (optional, default to 0)
     * @param limit Limit results (optional, default to 50)
     * @return MessagesSummary
     */
    public MessagesSummary messagesSummary(String query, Integer start, Integer limit) {
        try {
            String url = String.format("%s/api/v1/search?query=%s&start=%d&limit=%d",
                    baseUrl, query, Optional.ofNullable(start).orElse(0), Optional.ofNullable(limit).orElse(50));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), MessagesSummary.class);
            } else {
                throw new RuntimeException("Failed to fetch messages summary. Status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching messages summary", e);
        }
    }

    /**
     * Delete messages
     * If no IDs are provided then all messages are deleted.
     *
     * @param deleteRequest Object containing database IDs to delete (optional)
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(DeleteRequest deleteRequest) {
        try {
            String requestBody = objectMapper.writeValueAsString(deleteRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/messages"))
                    .method("DELETE", BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 && "ok".equalsIgnoreCase(response.body().trim());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error deleting messages", e);
        }
    }
}
