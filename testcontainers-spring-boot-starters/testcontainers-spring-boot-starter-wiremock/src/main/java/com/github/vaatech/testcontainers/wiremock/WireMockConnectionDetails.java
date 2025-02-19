package com.github.vaatech.testcontainers.wiremock;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface WireMockConnectionDetails extends ConnectionDetails {

    String url();

    String host();

    int port();
}
