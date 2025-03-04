package com.github.vaatech.testcontainers.autoconfigure.wiremock;

import com.github.vaatech.testcontainers.wiremock.WireMockProperties;

public class PropertiesWireMockConnectionDetails implements WireMockConnectionDetails {

    private final WireMockProperties properties;

    public PropertiesWireMockConnectionDetails(WireMockProperties properties) {
        this.properties = properties;
    }

    @Override
    public String url() {
        return properties.getBaseUrl();
    }

    @Override
    public String host() {
        return properties.getHost();
    }

    @Override
    public int port() {
        return properties.getPort();
    }
}
