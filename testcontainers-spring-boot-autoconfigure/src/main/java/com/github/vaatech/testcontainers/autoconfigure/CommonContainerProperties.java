package com.github.vaatech.testcontainers.autoconfigure;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.convert.DurationUnit;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class CommonContainerProperties {

    private boolean enabled;

    private DockerImage dockerImage;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration startupTimeout = Duration.ofSeconds(60);

    private int startupAttempts = 1;

    private boolean reuseContainer = false;

    private boolean usePullAlwaysPolicy = false;

    private boolean attachContainerLog = false;

    private String[] command;

    private Map<String, String> env = new HashMap<>();

    private Map<String, String> label = new HashMap<>();

    public abstract DockerImage getDefaultDockerImage();

    @Getter
    public static class DockerImage {

        private final DockerImageName dockerImageName;

        private final String registry;
        private final String name;
        private final String version;

        public DockerImage(String name) {
            var dockerImageName = DockerImageName.parse(name);

            this.dockerImageName = dockerImageName;
            this.registry = dockerImageName.getRegistry();
            this.name = dockerImageName.getUnversionedPart();
            this.version = dockerImageName.getVersionPart();
        }

        public String fullImageName() {
            return dockerImageName.asCanonicalNameString();
        }

        public static DockerImage create(String name) {
            return new DockerImage(name);
        }
    }
}
