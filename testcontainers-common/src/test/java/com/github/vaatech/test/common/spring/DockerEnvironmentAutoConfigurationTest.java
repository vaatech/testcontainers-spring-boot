package com.github.vaatech.test.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootTest(classes = DockerEnvironmentAutoConfigurationTest.TestConfiguration.class)
public class DockerEnvironmentAutoConfigurationTest {

  @Autowired ConfigurableListableBeanFactory beanFactory;

  @Autowired ConfigurableEnvironment environment;

  @EnableAutoConfiguration
  @Configuration
  static class TestConfiguration {}
}
