package com.simonjamesrowe.component.test.mongo;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

/**
 * Runs test containers for MongoDB
 */
public class MongoDBTestContainersExtension extends GenericContainer implements BeforeAllCallback {

    private static final String MONGO_USER = "user";
    private static final String MONGO_PASSWORD = UUID.randomUUID().toString();
    private static MongoDBTestContainersExtension container;

    public MongoDBTestContainersExtension() {
        super("mongo:4.0.18");
        withExposedPorts(27017);
        withEnv("MONGO_INITDB_ROOT_USERNAME", MONGO_USER);
        withEnv("MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD);
        waitingFor(
                Wait.forLogMessage(".*waiting for connections on port.*", 1)
        );

    }

    public void start() {
        super.start();
        System.setProperty("spring.data.mongodb.uri", String.format("mongodb://%s:%s@%s:%s/", MONGO_USER, MONGO_PASSWORD, DockerClientFactory
                .instance().dockerHostIpAddress(), container.getMappedPort(27017)));
        System.setProperty("spring.data.mongodb.authentication-database", "admin");
        System.setProperty("spring.data.mongodb.database", "test");
    }

    private static MongoDBTestContainersExtension getInstance() {
        if (container == null) {
            container = new MongoDBTestContainersExtension();
        }
        return container;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
