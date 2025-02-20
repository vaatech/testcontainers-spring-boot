package com.github.vaatech.testcontainers.autoconfigure;

public class DockerNotPresentException extends IllegalStateException {

    public DockerNotPresentException(String s) {
        super(s);
    }
}