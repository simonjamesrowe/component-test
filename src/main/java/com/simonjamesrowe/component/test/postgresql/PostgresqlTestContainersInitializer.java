package com.simonjamesrowe.component.test.postgresql;


import com.simonjamesrowe.component.test.TestContainersExtension;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

/**
 * Runs test containers for Postgres
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class PostgresqlTestContainersInitializer {

    private static int POSTGRESQL_PORT;
    private static final String POSTGRESQL_PASSWORD = UUID.randomUUID().toString();
    private static boolean initialized = false;
    private static GenericContainer postgesContainer;

    public PostgresqlTestContainersInitializer(WithPostgresContainer withPostgresContainer) {
        if (!initialized) {
            postgesContainer = new GenericContainer("postgres:9.6");
            postgesContainer.withExposedPorts(5432);
            postgesContainer.withEnv("POSTGRES_PASSWORD", POSTGRESQL_PASSWORD);
            postgesContainer.withEnv("POSTGRES_USER", "test");
            postgesContainer.withEnv("POSTGRES_DB", "test");
            postgesContainer.setWaitStrategy(Wait.forListeningPort());
            postgesContainer.withCommand(withPostgresContainer.command());
            initialize(withPostgresContainer);
        }
    }

    public void initialize(WithPostgresContainer withPostgresContainer) {
        if (!initialized) {
            TestContainersExtension.startContainer(postgesContainer);
            POSTGRESQL_PORT = postgesContainer.getMappedPort(5432);
        }
        initialized = true;
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.datasource.url", String.format("jdbc:postgresql://%s:%s/test?socketTimeout=300", DockerClientFactory.instance().dockerHostIpAddress(), POSTGRESQL_PORT));
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.password", POSTGRESQL_PASSWORD);
        System.setProperty("spring.datasource.hikari.maximum-pool-size", String.valueOf(withPostgresContainer.poolSize()));
    }
}
