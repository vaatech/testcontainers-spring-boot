package io.github.vaatech.testcontainers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.log.LogMessage;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.lifecycle.Startable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Order
class TestcontainersLifecycleBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    private static final Log logger = LogFactory.getLog(TestcontainersLifecycleBeanPostProcessor.class);

    private final ConfigurableListableBeanFactory beanFactory;

    private volatile boolean containersInitialized = false;

    TestcontainersLifecycleBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TestcontainersEnvironment testcontainersEnvironment) {
            testcontainersEnvironment.run();
        }
        if (this.beanFactory.isConfigurationFrozen()) {
            initializeContainers();
        }
        return bean;
    }

    private void initializeContainers() {
        if (this.containersInitialized) {
            return;
        }

        this.containersInitialized = true;
        Set<String> beanNames = new LinkedHashSet<>();
        beanNames.addAll(List.of(this.beanFactory.getBeanNamesForType(ContainerState.class, false, false)));
        beanNames.addAll(List.of(this.beanFactory.getBeanNamesForType(Startable.class, false, false)));
        beanNames.addAll(List.of(this.beanFactory.getBeanNamesForType(TestcontainersEnvironment.class, false, false)));
        for (String beanName : beanNames) {
            try {
                this.beanFactory.getBean(beanName);
            } catch (BeanCreationException ex) {
                if (ex.contains(BeanCurrentlyInCreationException.class)) {
                    this.containersInitialized = false;
                    return;
                }
                throw ex;
            }
        }
        if (!beanNames.isEmpty()) {
            logger.debug(LogMessage.format("Initialized container beans '%s'", beanNames));
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return bean instanceof Startable;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof Startable startable && !isDestroyedByFramework(beanName) && !isReusedContainer(bean)) {
            startable.close();
        }
    }

    private boolean isDestroyedByFramework(String beanName) {
        try {
            BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition(beanName);
            String destroyMethodName = beanDefinition.getDestroyMethodName();
            return !"".equals(destroyMethodName);
        } catch (NoSuchBeanDefinitionException ex) {
            return false;
        }
    }

    private boolean isReusedContainer(Object bean) {
        return (bean instanceof GenericContainer<?> container) && container.isShouldBeReused();
    }

}