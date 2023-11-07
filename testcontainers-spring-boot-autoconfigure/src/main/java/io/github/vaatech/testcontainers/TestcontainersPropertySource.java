package io.github.vaatech.testcontainers;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TestcontainersPropertySource extends EnumerablePropertySource<Map<String, Supplier<Object>>> {

	static final String NAME = "testcontainersPropertySource";

	private final DynamicPropertyRegistry registry;

	TestcontainersPropertySource() {
		this(Collections.synchronizedMap(new LinkedHashMap<>()));
	}

	private TestcontainersPropertySource(Map<String, Supplier<Object>> valueSuppliers) {
		super(NAME, Collections.unmodifiableMap(valueSuppliers));
		this.registry = (name, valueSupplier) -> {
			Assert.hasText(name, "'name' must not be null or blank");
			Assert.notNull(valueSupplier, "'valueSupplier' must not be null");
			valueSuppliers.put(name, valueSupplier);
		};
	}

	@Override
	public Object getProperty(String name) {
		Supplier<Object> valueSupplier = this.source.get(name);
		return (valueSupplier != null) ? valueSupplier.get() : null;
	}

	@Override
	public boolean containsProperty(String name) {
		return this.source.containsKey(name);
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.keySet());
	}

	DynamicPropertyRegistry getRegistry() {
		return registry;
	}
}