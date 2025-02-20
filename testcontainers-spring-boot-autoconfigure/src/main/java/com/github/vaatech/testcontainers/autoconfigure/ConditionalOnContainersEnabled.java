package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnExpression("${containers.enabled:true}")
public @interface ConditionalOnContainersEnabled {

}
