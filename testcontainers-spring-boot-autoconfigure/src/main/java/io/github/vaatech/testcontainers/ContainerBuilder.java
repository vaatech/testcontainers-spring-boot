package io.github.vaatech.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Network;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContainerBuilder<C extends Container<?>> {
    private final AtomicBoolean built = new AtomicBoolean();

    private Supplier<C> containerSupplier;
    private C container;
    private DynamicPropertyRegistry registry;
    private Network network;

    private Consumer<C> configurer;

    private BiConsumer<DynamicPropertyRegistry, C> registryConfigurers;

    ContainerBuilder(Network network, DynamicPropertyRegistry registry) {
        this.network = network;
        this.registry = registry;
    }

    public static <C extends Container<?>> ContainerBuilder<C> from(Network network, DynamicPropertyRegistry registry) {
        return from(network, registry);
    }

    public ContainerBuilder<C> configure(Consumer<C> configurer) {
        this.configurer = configurer;
        return this;
    }

    public ContainerBuilder<C> register(BiConsumer<DynamicPropertyRegistry, C> configurer) {
        this.registryConfigurers = configurer;
        return this;
    }

    public ContainerBuilder<C> containerSuppler(Supplier<C> supplier) {
        this.containerSupplier = supplier;
        return this;
    }

    public final C build() {
        if (this.built.compareAndSet(false, true)) {
            return doBuild();
        }
        throw new IllegalStateException("This container has already been built");
    }

    public final C get() {
        if (!this.built.get()) {
            throw new IllegalStateException("This container has not been built");
        }
        return this.container;
    }

    public C getOrBuild() {
        if (this.built.get()) {
            return get();
        }
        try {
            return build();
        } catch (Exception ex) {
            return null;
        }
    }

    private C doBuild() {
        this.container = containerSupplier.get();

        if (this.container == null)
            throw new IllegalStateException("Container supplier cannot return null");

        if (this.configurer != null)
            this.configurer.accept(container);

        if (this.registryConfigurers != null)
            this.registryConfigurers.accept(registry, container);

        if (network == null)
            this.network = Network.newNetwork();

        this.container.withNetwork(network);

        return container;
    }
}
