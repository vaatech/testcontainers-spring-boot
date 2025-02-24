package com.github.vaatech.testcontainers.autoconfigure.postgresql;

import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@EnabledIf(expression = "#{environment['container.postgresql.enabled'] && environment['containers.enabled']}", loadContext = true)
public @interface EnabledIfPostgreSQLContainer {
}
