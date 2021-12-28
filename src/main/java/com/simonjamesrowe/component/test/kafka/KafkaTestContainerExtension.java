package com.simonjamesrowe.component.test.kafka;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Runs test containers for Elasticsearch
 */
public class KafkaTestContainerExtension extends KafkaContainer implements BeforeAllCallback {

    private static KafkaTestContainerExtension container;

    public KafkaTestContainerExtension() {
        super(DockerImageName.parse("confluentinc/cp-kafka"));
        setWaitStrategy(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
    }

    @Override
    public void start() {
        super.start();
        System.setProperty(
                "spring.kafka.bootstrap-servers",
                String.format(
                        "%s:%s",
                        DockerClientFactory.instance().dockerHostIpAddress(),
                        container.getMappedPort(9093)
                )
        );
    }

    private static KafkaTestContainerExtension getInstance() {
        if (container == null) {
            container = new KafkaTestContainerExtension();
        }
        return container;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}