package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import com.github.vaatech.testcontainers.mailpit.MailPitProperties;

public class PropertiesMailPitConnectionDetails implements MailPitConnectionDetails {

    private final MailPitProperties properties;

    public PropertiesMailPitConnectionDetails(MailPitProperties properties) {
        this.properties = properties;
    }

    @Override
    public String host() {
        return properties.getHost();
    }

    @Override
    public int portHttp() {
        return properties.getPortHttp();
    }

    @Override
    public int portSMTP() {
        return properties.getPortSmtp();
    }

    @Override
    public String serverUrl() {
        return String.format("http://%s:%d", host(), portHttp());
    }
}
