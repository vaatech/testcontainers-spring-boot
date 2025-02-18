package com.github.vaatech.testcontainers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import static com.github.vaatech.testcontainers.TestcontainersPropertySource.NAME;

@ConditionalOnClass(DynamicPropertyRegistry.class)
public class TestcontainersPropertySourceAutoConfiguration {

    @Bean
    DynamicPropertyRegistry dynamicPropertyRegistry(ConfigurableEnvironment environment) {
        PropertySource<?> propertySource = environment.getPropertySources().get(NAME);

        if (propertySource == null) {
            var testcontainersPropertySource = new TestcontainersPropertySource();
            environment.getPropertySources().addFirst(testcontainersPropertySource);
            return testcontainersPropertySource.getRegistry();
        }

        if (propertySource instanceof TestcontainersPropertySource testcontainersPropertySource) {
            return testcontainersPropertySource.getRegistry();
        }

        throw new IllegalStateException("Incorrect DynamicValuesPropertySource type registered");
    }

}
