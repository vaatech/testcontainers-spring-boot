package com.github.vaatech.testcontainers;

public class DockerNotPresentException extends IllegalStateException {

    public DockerNotPresentException(String s) {
        super(s);
    }
}