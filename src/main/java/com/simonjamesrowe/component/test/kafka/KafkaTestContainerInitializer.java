package com.simonjamesrowe.component.test.kafka;

import com.simonjamesrowe.component.test.TestContainersExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Runs test containers for Elasticsearch
 */
public class KafkaTestContainerInitializer {

    private static int PORT;
    private static boolean initialized = false;
    private static KafkaContainer kafkaContainer;

    public KafkaTestContainerInitializer(WithKafkaContainer withKafkaContainer) {
        if (!initialized) {
            kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
            kafkaContainer.setWaitStrategy(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
            initialize();
        }
    }

    public void initialize() {
        if (!initialized) {
            TestContainersExtension.startContainer(kafkaContainer);
            PORT = kafkaContainer.getMappedPort(9093);
        }
        initialized = true;
        System.setProperty(
                "spring.kafka.bootstrap-servers",
                String.format(
                        "%s:%s",
                        DockerClientFactory.instance().dockerHostIpAddress(),
                        PORT
                )
        );
    }
}