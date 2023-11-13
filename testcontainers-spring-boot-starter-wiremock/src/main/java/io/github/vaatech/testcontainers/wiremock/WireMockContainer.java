package io.github.vaatech.testcontainers.wiremock;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

public class WireMockContainer extends GenericContainer<WireMockContainer> {

    private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse("wiremock/wiremock");
    public static final int DEFAULT_HTTP_PORT = 8080;

    private int port = DEFAULT_HTTP_PORT;
    private boolean disableBanner = true;
    private boolean verbose;

    public WireMockContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE);

        this.waitingFor(Wait.forHttp("/__admin")
                .withMethod("GET")
                .forStatusCode(200));
    }

    @Override
    protected void configure() {
        this.addExposedPorts(port);

        List<String> commandParts = new ArrayList<>();

        if (disableBanner) {
            commandParts.add("--disable-banner");
        }

        if (verbose) {
            commandParts.add("--verbose");
        }

        commandParts.add("--port=" + port);

        this.setCommand(commandParts.toArray(new String[0]));
    }

    public WireMockContainer withPort(int port) {
        this.port = port;
        return this;
    }

    public WireMockContainer withBannerDisabled(boolean disableBanner) {
        this.disableBanner = disableBanner;
        return this;
    }

    public WireMockContainer withVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }
}
