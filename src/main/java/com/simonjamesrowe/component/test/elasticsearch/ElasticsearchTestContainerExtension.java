package com.simonjamesrowe.component.test.elasticsearch;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

/**
 * Runs test containers for Elasticsearch
 */
public class ElasticsearchTestContainerExtension extends ElasticsearchContainer implements BeforeAllCallback {
    public static final String CONTAINER_NAME = "docker.elastic.co/elasticsearch/elasticsearch:7.9.2";
    private static ElasticsearchTestContainerExtension container;

    public ElasticsearchTestContainerExtension() {
        super(CONTAINER_NAME);
    }

    private ElasticsearchContainer getInstance() {
        if (container == null) {
            container = new ElasticsearchTestContainerExtension();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        String elasticSearchUrl = String.format(
                "http://%s:%s",
                DockerClientFactory.instance().dockerHostIpAddress(),
                container.getMappedPort(9200)
        );
        String reactiveElasticSearchUrl = String.format(
                "%s:%s",
                DockerClientFactory.instance().dockerHostIpAddress(),
                container.getMappedPort(9200)
        );
        System.setProperty(
                "spring.data.elasticsearch.client.reactive.endpoints",
                reactiveElasticSearchUrl
        );
        System.setProperty(
                "spring.elasticsearch.rest.uris",
                elasticSearchUrl
        );
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
