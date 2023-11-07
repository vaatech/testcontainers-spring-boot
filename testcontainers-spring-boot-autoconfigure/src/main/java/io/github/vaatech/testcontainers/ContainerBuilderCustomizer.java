package io.github.vaatech.testcontainers;

@FunctionalInterface
public interface ContainerBuilderCustomizer<B extends ContainerBuilder<?>> {

    void customize(B builder);

}
