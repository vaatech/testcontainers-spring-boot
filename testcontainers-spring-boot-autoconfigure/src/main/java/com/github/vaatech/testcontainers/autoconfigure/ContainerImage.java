package com.github.vaatech.testcontainers.autoconfigure;

public enum ContainerImage {

    /**
     * A container image suitable for testing SMTP.
     */
    MAILPIT("axllent/mailpit", "v1.22.3"),

    /**
     * A container image suitable for testing MySQL.
     */
    MYSQL("mysql", "8.0"),

    /**
     * A container image suitable for testing Postgres.
     */
    POSTGRESQL("postgres", "16"),

    /**
     * A container image suitable for testing with WireMock.
     */
    WIREMOCK("wiremock/wiremock", "3.7.0");

    private final String name;
    private final String tag;

    ContainerImage(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    @Override
    public String toString() {
        return (this.tag != null) ? this.name + ":" + this.tag : this.name;
    }
}
