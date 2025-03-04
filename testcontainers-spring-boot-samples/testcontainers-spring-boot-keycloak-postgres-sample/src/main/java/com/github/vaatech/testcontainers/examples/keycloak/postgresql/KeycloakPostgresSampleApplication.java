package com.github.vaatech.testcontainers.examples.keycloak.postgresql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@SpringBootApplication
public class KeycloakPostgresSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakPostgresSampleApplication.class, args);
	}

	@Controller
	static class TestController {

		@GetMapping("/")
		public String home(Model model, @AuthenticationPrincipal OidcUser user) {
			model.addAttribute("name", user.getClaim("preferred_username"));
			model.addAttribute("email", user.getEmail());
			model.addAttribute("subject", user.getSubject());
			return "public";
		}
	}
}
