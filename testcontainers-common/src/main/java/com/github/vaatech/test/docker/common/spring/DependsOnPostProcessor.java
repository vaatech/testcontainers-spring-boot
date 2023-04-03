package com.github.vaatech.test.docker.common.spring;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

public class DependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
  public DependsOnPostProcessor(Class<?> beanClass, String... dependsOn) {
    super(beanClass, dependsOn);
  }
}
