package com.github.vaatech.testcontainers.autoconfigure.mailpit;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${container.mailpit.port-http}")
public @interface MailPitPortHTTP {
}
