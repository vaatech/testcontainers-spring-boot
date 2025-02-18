package com.github.vaatech.testcontainers.mailpit;

import com.github.vaatech.testcontainers.ConditionalOnContainersEnabled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnContainersEnabled
@ConditionalOnProperty(name = "container.mailpit.enabled", matchIfMissing = true)
public @interface ConditionalOnMailPitContainerEnabled {

}
