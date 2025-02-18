package com.github.vaatech.testcontainers;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.DockerClientFactory;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class TestcontainersLifecycleApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Set<ConfigurableApplicationContext> applied = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        var enabled = applicationContext.getEnvironment().getProperty("containers.enabled", Boolean.class, true);
        if (!enabled) return;

        boolean dockerPresent = DockerClientFactory.instance().isDockerAvailable();
        if (!dockerPresent) {
            throw new DockerNotPresentException("Docker must be present in order for testcontainers to work properly!");
        }

        synchronized (applied) {
            if (!applied.add(applicationContext)) {
                return;
            }
        }
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        applicationContext.addBeanFactoryPostProcessor(new TestcontainersLifecycleBeanFactoryPostProcessor());
        beanFactory.addBeanPostProcessor(new TestcontainersLifecycleBeanPostProcessor(beanFactory));
    }

}
