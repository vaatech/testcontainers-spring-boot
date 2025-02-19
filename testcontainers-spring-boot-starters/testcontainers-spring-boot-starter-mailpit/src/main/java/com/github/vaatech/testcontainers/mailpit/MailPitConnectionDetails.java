package com.github.vaatech.testcontainers.mailpit;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface MailPitConnectionDetails extends ConnectionDetails {
    String host();

    int portHttp();

    int portSMTP();

    String serverUrl();

}
