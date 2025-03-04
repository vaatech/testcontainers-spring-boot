package com.github.vaatech.testcontainers.keycloak;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

public class KeycloakContainer extends ExtendableKeycloakContainer<KeycloakContainer> {

    public KeycloakContainer(final DockerImageName dockerImageName) {
        super(Objects.requireNonNull(dockerImageName,"DockerImageName is required").toString());
    }
}
