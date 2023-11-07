package io.github.vaatech.testcontainers.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.springframework.boot.autoconfigure.AutoConfigureOrder.DEFAULT_ORDER;

@Slf4j
@Configuration
@AutoConfigureOrder(DEFAULT_ORDER)
public class EchoContainersAutoConfiguration {

    @Bean
    @SuppressWarnings("resource")
    EchoContainer echoContainer1() {
        return new EchoContainer()
                .withCommand("/bin/sh", "-c", "while true; do echo 'Press [CTRL+C] to stop..'; sleep 1; done")
//                .waitingFor(new PositiveCommandWaitStrategy())
                .withStartupTimeout(Duration.ofSeconds(15));
    }

    @Bean
    EchoContainer echoContainer2() {
        return new EchoContainer()
                .withCommand("/bin/sh", "-c", "while true; do echo 'Press [CTRL+C] to stop..'; sleep 1; done")
//                .waitingFor(new PositiveCommandWaitStrategy())
                .withStartupTimeout(Duration.ofSeconds(15));
    }

}
