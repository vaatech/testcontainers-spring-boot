package com.github.vaatech.testcontainers.core.checks;

import lombok.extern.slf4j.Slf4j;
import org.rnorth.ducttape.TimeoutException;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Slf4j
public abstract class AbstractRetryingWaitStrategy extends AbstractWaitStrategy {

    protected String getContainerType() {
        return getClass().getSimpleName();
    }

    @Override
    protected void waitUntilReady() {
        long seconds = startupTimeout.getSeconds();
        try {
            Unreliables.retryUntilTrue((int) seconds, TimeUnit.SECONDS,
                    () -> getRateLimiter().getWhenReady(this::isReady));
        } catch (TimeoutException e) {
            throw new ContainerLaunchException(
                    format("[%s] notifies that container[%s] is not ready after [%d] seconds, container cannot be started.",
                            getContainerType(), waitStrategyTarget.getContainerId(), seconds));
        }
    }

    protected abstract boolean isReady();

    public static Container.ExecResult executeInContainer(ContainerState container, String... command) {
        try {
            return container.execInContainer(command);
        } catch (Exception e) {
            String format = String.format("Exception was thrown when executing: %s, for container: %s ", Arrays.toString(command), container.getContainerId());
            throw new IllegalStateException(format, e);
        }
    }

    public static Container.ExecResult executeAndCheckExitCode(ContainerState container, String... command) {
        try {
            Container.ExecResult execResult = container.execInContainer(command);
            log.debug("Executed command in container: {} with result: {}", container.getContainerId(), execResult);
            if (execResult.getExitCode() != 0) {
                throw new IllegalStateException("Failed to execute command. Execution result: " + execResult);
            }
            return execResult;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute command in container: " + container.getContainerId(), e);
        }
    }
}