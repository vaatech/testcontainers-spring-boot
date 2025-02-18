package com.github.vaatech.testcontainers;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.github.vaatech.testcontainers.TestcontainersEnvironmentAutoConfiguration.TESTCONTAINERS_ENVIRONMENT;

public class DependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public DependsOnPostProcessor(Class<?> beanClass, String... dependsOn) {
        super(beanClass,
                Stream.concat(Stream.of(TESTCONTAINERS_ENVIRONMENT), Arrays.stream(dependsOn)).toArray(String[]::new));
    }
}
