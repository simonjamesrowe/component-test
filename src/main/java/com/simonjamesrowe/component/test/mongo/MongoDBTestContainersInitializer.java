package com.simonjamesrowe.component.test.mongo;

import com.simonjamesrowe.component.test.TestContainersExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

/**
 * Runs test containers for MongoDB
 */
public class MongoDBTestContainersInitializer  {

    private static int MONGO_PORT;
    private static final String MONGO_USER = "user";
    private static final String MONGO_PASSWORD = UUID.randomUUID().toString();
    private static GenericContainer mongoContainer;
    private static boolean initialized = false;


    public MongoDBTestContainersInitializer(WithMongoDBContainer withMongoDBContainer) {
        if (!initialized) {
            mongoContainer = new GenericContainer("mongo:4.0.18");
            mongoContainer = mongoContainer.withExposedPorts(27017);
            mongoContainer = mongoContainer.withEnv("MONGO_INITDB_ROOT_USERNAME", MONGO_USER);
            mongoContainer = mongoContainer.withEnv("MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD);
            mongoContainer = mongoContainer.waitingFor(
                    Wait.forLogMessage(".*waiting for connections on port.*", 1)
            );
            initialize();
        }
    }
 
    public void initialize() {
        if (!initialized) {
            TestContainersExtension.startContainer(mongoContainer);
            MONGO_PORT = mongoContainer.getMappedPort(27017);
        }
        initialized = true;
        System.setProperty("spring.data.mongodb.uri", String.format("mongodb://%s:%s@%s:%s/",MONGO_USER, MONGO_PASSWORD, DockerClientFactory
            .instance().dockerHostIpAddress(), MONGO_PORT));
        System.setProperty("spring.data.mongodb.authentication-database", "admin");
        System.setProperty("spring.data.mongodb.database", "test");
    }
}
