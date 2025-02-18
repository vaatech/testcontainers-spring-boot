package com.github.vaatech.testcontainers.mailpit.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaatech.testcontainers.mailpit.model.AppInformation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApplicationRestApi {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApplicationRestApi(HttpClient httpClient,
                              ObjectMapper objectMapper,
                              String host,
                              int port) {

        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = "http://" + host + ":" + port;
    }

    /**
     * Get application information
     * Returns basic runtime information, message totals and latest release version.
     *
     * @return AppInformation
     */
    public AppInformation appInformation() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/info"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), AppInformation.class);
            } else {
                throw new RuntimeException("Request failed with status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving application info", e);
        }
    }

}
