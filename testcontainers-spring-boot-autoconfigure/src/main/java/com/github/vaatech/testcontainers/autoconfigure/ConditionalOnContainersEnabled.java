package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnExpression("${containers.enabled:true}")
@ConditionalOnDockerPresence
public @interface ConditionalOnContainersEnabled {

}
