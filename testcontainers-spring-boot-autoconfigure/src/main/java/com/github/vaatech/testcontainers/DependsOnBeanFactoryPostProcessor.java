package com.github.vaatech.testcontainers;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

public class DependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {

    public DependsOnBeanFactoryPostProcessor(Class<?> beanClass, String... dependsOn) {
        super(beanClass, dependsOn);
    }
}
