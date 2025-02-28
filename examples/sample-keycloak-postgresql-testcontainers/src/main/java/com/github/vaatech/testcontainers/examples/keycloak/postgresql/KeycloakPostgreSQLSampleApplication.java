package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class KeycloakPostgreSQLSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakPostgreSQLSampleApplication.class, args);
	}

	@RestController
	static class TestController {

		@GetMapping
		public String home() {
			return "hello";
		}
	}
}
