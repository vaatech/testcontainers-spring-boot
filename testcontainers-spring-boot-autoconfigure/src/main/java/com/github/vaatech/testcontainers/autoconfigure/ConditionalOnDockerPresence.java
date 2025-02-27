package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(DockerPresenceCondition.class)
public @interface ConditionalOnDockerPresence {
}
