package com.github.vaatech.testcontainers;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.annotation.Order;
import org.testcontainers.lifecycle.Startable;

@Order
class TestcontainersLifecycleBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanNamesForType(Startable.class, false, false)) {
            try {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                String destroyMethodName = beanDefinition.getDestroyMethodName();
                if (destroyMethodName == null || AbstractBeanDefinition.INFER_METHOD.equals(destroyMethodName)) {
                    beanDefinition.setDestroyMethodName("");
                }
            } catch (NoSuchBeanDefinitionException ignored) {}
        }
    }

}
