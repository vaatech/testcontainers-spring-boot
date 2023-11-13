package io.github.vaatech.testcontainers.artemis;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class ArtemisContainer extends GenericContainer<ArtemisContainer> {

    private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse("apache/activemq-artemis");
    public static final int DEFAULT_BROKER_PORT = 61616;
    public static final int DEFAULT_HTTP_PORT = 8161;

    private String username = "artemis";
    private String password = "artemis";
    private boolean anonymousLogin = false;
    private String extraArgs = "";

    public ArtemisContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE);

        this.addExposedPorts(DEFAULT_BROKER_PORT, DEFAULT_HTTP_PORT);
        this.waitingFor(Wait.forLogMessage(".*HTTP Server started.*", 1).withStartupTimeout(Duration.ofMinutes(1)));
    }

    @Override
    protected void configure() {
        this.addEnv("ARTEMIS_USER", this.username);
        this.addEnv("ARTEMIS_PASSWORD", this.password);

        if (this.anonymousLogin)
            this.addEnv("ANONYMOUS_LOGIN", "true");


        if (!this.extraArgs.isBlank())
            this.addEnv("EXTRA_ARGS", this.extraArgs);
    }

    public ArtemisContainer withUser(String username) {
        this.username = username;
        return this;
    }

    public ArtemisContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    public ArtemisContainer withAnonymousLogin(boolean anonymousLogin) {
        this.anonymousLogin = anonymousLogin;
        return this;
    }

    public ArtemisContainer withExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
        return this;
    }

    public String getBrokerUrl() {
        return String.format("tcp://%s:%s", getHost(), getMappedPort(DEFAULT_BROKER_PORT));
    }

    public String consoleUrl() {
        return String.format("http://%s:%s", getHost(), getMappedPort(DEFAULT_HTTP_PORT));
    }

    public String getUser() {
        return getEnvMap().get("ARTEMIS_USER");
    }

    public String getPassword() {
        return getEnvMap().get("ARTEMIS_PASSWORD");
    }
}
