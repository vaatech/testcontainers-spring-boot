package com.github.vaatech.testcontainers.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.testcontainers.DockerClientFactory;

public class DockerPresenceCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("DockerPresenceMarker");
        if (DockerClientFactory.instance().isDockerAvailable()) {
            return ConditionOutcome.match(message.foundExactly("Docker Engine"));
        }
        return ConditionOutcome.noMatch(message.didNotFind("Docker Engine").atAll());
    }
}
