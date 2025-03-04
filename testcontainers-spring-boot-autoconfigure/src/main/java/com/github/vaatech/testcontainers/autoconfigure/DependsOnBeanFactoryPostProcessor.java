package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

public class DependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {

    public DependsOnBeanFactoryPostProcessor(Class<?> beanClass, String... dependsOn) {
        super(beanClass, dependsOn);
    }
}
