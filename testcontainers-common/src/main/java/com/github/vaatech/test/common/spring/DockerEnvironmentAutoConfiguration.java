package com.github.vaatech.test.common.spring;

import com.github.vaatech.test.common.util.ContainerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(value = Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = "containers.enabled", matchIfMissing = true)
public class DockerEnvironmentAutoConfiguration {

  private static final Logger LOGGER =
      LogManager.getFormatterLogger(DockerEnvironmentAutoConfiguration.class);

  public static final String DOCKER_ENVIRONMENT = "dockerEnvironment";

  private final ConfigurableEnvironment environment;

  public DockerEnvironmentAutoConfiguration(ConfigurableEnvironment environment) {
    this.environment = environment;
  }

  @Bean
  @ConditionalOnMissingBean(Network.class)
  public Network network() {
    Network network = Network.newNetwork();
    log.info("Created docker Network with id={}", network.getId());
    return network;
  }

  @Bean(name = DOCKER_ENVIRONMENT)
  public DockerEnvironment dockerContainers(ObjectProvider<DockerContainer[]> containersProvider)
      throws Exception {

    var containers =
        Optional.ofNullable(containersProvider.getIfAvailable()).orElse(new DockerContainer[0]);

    if (containers.length == 0) {
      return new DockerEnvironment(false, new GenericContainer<?>[0]);
    }

    Arrays.stream(containers).forEach(c -> c.setEnvironment(environment));

    ContainerUtils.run(containers);
    //    Callable<Boolean> allHealthy =
    //        () -> Arrays.stream(containers).allMatch(ContainerState::isHealthy);
    //    await().atMost(30, SECONDS).pollInterval(500, MICROSECONDS).until(allHealthy);

    Arrays.stream(containers).forEach(c -> ContainerUtils.logContainerInfo(c.name(), c, LOGGER));

    return new DockerEnvironment(
        /*allHealthy.call()*/ true,
        Arrays.stream(containers).map(DockerContainer::unwrap).toArray(GenericContainer<?>[]::new));
  }
}
