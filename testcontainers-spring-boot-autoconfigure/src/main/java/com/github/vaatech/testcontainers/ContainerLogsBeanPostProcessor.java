package com.github.vaatech.testcontainers;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerLoggerFactory;

import java.util.function.Consumer;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContainerLogsBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Container<?> container) {
            var logger = logger(container);
            var logConsumer = containerLogsConsumer(logger);
            container.withLogConsumer(logConsumer);
        }
        return bean;
    }

    private static Logger logger(Container<?> container) {
        return DockerLoggerFactory.getLogger(container.getDockerImageName());
    }

    private static Consumer<OutputFrame> containerLogsConsumer(final Logger logger) {
        return new Slf4jLogConsumer(logger, true);
    }
}