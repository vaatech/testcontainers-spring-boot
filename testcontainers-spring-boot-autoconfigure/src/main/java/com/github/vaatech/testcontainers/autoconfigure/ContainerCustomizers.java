package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.util.LambdaSafe;
import org.testcontainers.containers.GenericContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContainerCustomizers<T extends GenericContainer<?>, C extends ContainerCustomizer<T>> {

    private final List<C> customizers;

    public ContainerCustomizers(ObjectProvider<? extends C> customizers) {
        this.customizers = (customizers != null)
                ? new ArrayList<>(customizers.orderedStream().toList())
                : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public T customize(T container) {
        LambdaSafe.callbacks(ContainerCustomizer.class, this.customizers, container)
                .withLogger(this.getClass())
                .invoke((customizer) -> customizer.customize(container));
        return container;
    }
}
