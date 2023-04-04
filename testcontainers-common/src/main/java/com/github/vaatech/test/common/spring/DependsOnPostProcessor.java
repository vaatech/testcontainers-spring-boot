package com.github.vaatech.test.common.spring;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;

import static com.github.vaatech.test.common.spring.DockerEnvironmentAutoConfiguration.DOCKER_ENVIRONMENT;

public class DependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
  public DependsOnPostProcessor(Class<?> beanClass, String... dependsOn) {
    super(beanClass, makeDependsOn(dependsOn));
  }

  static String[] makeDependsOn(String... dependsOn) {
    String[] result = new String[dependsOn.length + 1];
    result[0] = DOCKER_ENVIRONMENT;
    System.arraycopy(dependsOn, 0, result, 1, dependsOn.length);
    return result;
  }
}
