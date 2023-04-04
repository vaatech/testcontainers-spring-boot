package com.github.vaatech.test.common.spring;

public class DockerNotPresentException extends IllegalStateException {
    public DockerNotPresentException(String s) {
        super(s);
    }
}