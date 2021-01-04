package com.simonjamesrowe.component.test.elasticsearch;

import com.simonjamesrowe.component.test.TestContainersExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.time.Duration;

/**
 * Runs test containers for Elasticsearch
 */
public class ElasticsearchTestContainerInitializer {

    private static int PORT;
    private static boolean initialized = false;
    private static ElasticsearchContainer elasticsearchContainer;

    public ElasticsearchTestContainerInitializer(WithElasticsearchContainer withElasticsearchContainer) {
        if (!initialized) {
            elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.7.1");
            elasticsearchContainer.setWaitStrategy(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));
            initialize();
        }
    }

    public void initialize() {
        if (!initialized) {
            TestContainersExtension.startContainer(elasticsearchContainer);
            PORT = elasticsearchContainer.getMappedPort(9200);
        }
        initialized = true;
        System.setProperty(
                "elasticsearch.url",
                String.format(
                        "http://%s:%s",
                        DockerClientFactory.instance().dockerHostIpAddress(),
                        PORT
                )
        );
    }
}