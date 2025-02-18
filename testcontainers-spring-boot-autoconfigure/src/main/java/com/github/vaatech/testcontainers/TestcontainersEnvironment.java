package com.github.vaatech.testcontainers;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.vaatech.testcontainers.util.DateUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
public record TestcontainersEnvironment(GenericContainer<?>[] containers,
                                        Network network) {

    public void run() {

        try {
            Startables.deepStart(Arrays.stream(containers).map(StartableDecorator::new)).join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (GenericContainer<?> container : containers) {
            logContainerInfo(container.getContainerId(), container);
        }
    }

    private static void logContainerInfo(String name, GenericContainer<?> container) {
        String offset = "";
        log.info("{}{}:", offset, name);
        List<Integer> exposedPorts = new ArrayList<>(container.getExposedPorts());
        exposedPorts.sort(Comparator.naturalOrder());

        offset += "\t";
        log.info("{}Host: {}", offset, container.getHost());
        if (!exposedPorts.isEmpty()) {
            log.info("{}Ports:", offset);
        }

        offset += "\t";
        for (Integer port : exposedPorts) {
            Integer mappedPort = container.getMappedPort(port);
            log.info("{}{} -> {}", offset, port, Objects.toString(mappedPort, "NONE"));
        }
    }

    static class StartableDecorator implements Startable {

        private final GenericContainer<?> container;

        StartableDecorator(GenericContainer<?> container) {
            this.container = container;
        }

        @Override
        public Set<Startable> getDependencies() {
            return container.getDependencies();
        }

        @Override
        public void start() {
            startAndLogTime(container, log);
        }

        @Override
        public void stop() {
            container.stop();
        }

        @Override
        public void close() {
            container.close();
        }

        private static void startAndLogTime(GenericContainer<?> container, Logger logger) {
            Instant startTime = Instant.now();
            container.start();
            long startupTime = Duration.between(startTime, Instant.now()).toMillis() / 1000;

            String dockerImageName = container.getDockerImageName();
            String buildDate = getBuildDate(container, dockerImageName);
            // influxdb:1.4.3 build 2018-07-06T17:25:49+02:00 (2 years 11 months ago) startup time is 21 seconds
            if (startupTime < 10L) {
                logger.info("{} build {} startup time is {} seconds", dockerImageName, buildDate, startupTime);
            } else if (startupTime < 20L) {
                logger.warn("{} build {} startup time is {} seconds", dockerImageName, buildDate, startupTime);
            } else {
                logger.error("{} build {} startup time is {} seconds", dockerImageName, buildDate, startupTime);
            }
        }

        private static String getBuildDate(GenericContainer<?> container, String dockerImageName) {
            String imageResponseCreated = null;
            try {
                InspectImageResponse inspectImageResponse = container.getDockerClient().inspectImageCmd(dockerImageName).exec();
                if (inspectImageResponse != null) {
                    imageResponseCreated = inspectImageResponse.getCreated();
                    return DateUtils.toDateAndTimeAgo(imageResponseCreated);
                } else {
                    log.error("InspectImageResponse was null");
                }
            } catch (NotFoundException e) {
                log.error("Could not get InspectImageResponse", e);
            }
            return imageResponseCreated;
        }

    }


}
