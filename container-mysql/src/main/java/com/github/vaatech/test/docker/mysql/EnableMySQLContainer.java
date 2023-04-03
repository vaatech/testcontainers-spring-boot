package com.github.vaatech.test.docker.mysql;

import com.github.vaatech.test.docker.common.spring.EnableEmbeddedContainers;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableEmbeddedContainers
@Import(MySQLContainerConfiguration.class)
public @interface EnableMySQLContainer {
}
