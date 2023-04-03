package com.github.vaatech.test.docker.common.spring;

import com.github.vaatech.test.docker.common.spring.DockerContainersConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
//@Configuration
//@ComponentScan(basePackages = "com.github.vaatech.test")
//@ComponentScan(basePackages = "com.github.vaatech.test")
@Import(DockerContainersConfiguration.class)
public @interface EnableEmbeddedContainers {
}
