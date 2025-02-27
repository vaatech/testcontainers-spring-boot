package com.github.vaatech.testcontainers.autoconfigure;

import com.github.dockerjava.api.model.Capability;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.StringUtils;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    private Map<String, String> env = new LinkedHashMap<>();

    private Map<String, String> label = new LinkedHashMap<>();

    private List<MountVolume> mountVolumes = new ArrayList<>();

    private List<Capability> capabilities = new ArrayList<>();

    private List<TmpFsMount> tmpFs = new ArrayList<>();

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

    public record MountVolume(@NotBlank String path,
                              @NotBlank String containerPath,
                              @NotNull Integer mode,
                              @NotNull VolumeType type) {

        public MountVolume {
            if (!StringUtils.hasText(path))
                throw new IllegalArgumentException("path is required");

            if (!StringUtils.hasText(containerPath))
                throw new IllegalArgumentException("container path is required");

            if (mode == null)
                mode = 444;

            if (type == null)
                throw new IllegalArgumentException("type is required");
        }
    }

    public enum VolumeType {
        CLASSPATH,
        HOST
    }

    public record TmpFsMount(String folder, String options) {

        public TmpFsMount {
            if (!StringUtils.hasText(folder))
                throw new IllegalArgumentException("folder is required");
        }

    }
}
