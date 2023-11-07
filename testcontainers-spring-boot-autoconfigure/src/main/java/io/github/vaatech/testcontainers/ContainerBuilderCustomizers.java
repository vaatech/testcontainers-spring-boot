package io.github.vaatech.testcontainers;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.util.LambdaSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ContainerBuilderCustomizers<B extends ContainerBuilder<?>, C extends ContainerBuilderCustomizer<?>> {

    private final List<C> customizers;

    public ContainerBuilderCustomizers(ObjectProvider<? extends C> customizers) {
        this.customizers = (customizers != null)
                ? new ArrayList<>(customizers.orderedStream().toList())
                : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public B customize(B builder) {
        LambdaSafe.callbacks(ContainerBuilderCustomizer.class, this.customizers, builder)
                .withLogger(this.getClass())
                .invoke((customizer) -> customizer.customize(builder));
        return builder;
    }
}
