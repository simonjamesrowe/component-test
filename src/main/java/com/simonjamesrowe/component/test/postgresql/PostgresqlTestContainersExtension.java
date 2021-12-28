package com.simonjamesrowe.component.test.postgresql;


import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

/**
 * Runs test containers for Postgres
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class PostgresqlTestContainersExtension extends GenericContainer implements BeforeAllCallback {

    private static final String POSTGRESQL_PASSWORD = UUID.randomUUID().toString();
    private static PostgresqlTestContainersExtension container;

    public PostgresqlTestContainersExtension() {
        super("postgres:9.6");
        withExposedPorts(5432);
        withEnv("POSTGRES_PASSWORD", POSTGRESQL_PASSWORD);
        withEnv("POSTGRES_USER", "test");
        withEnv("POSTGRES_DB", "test");
        setWaitStrategy(Wait.forListeningPort());
        withCommand("postgres", "-c", "max_connections=500", "-c", "shared_buffers=256MB");
    }

    public void start() {
        super.start();
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        System.setProperty("spring.datasource.url", String.format("jdbc:postgresql://%s:%s/test?socketTimeout=300", DockerClientFactory.instance().dockerHostIpAddress(), container.getMappedPort(5432)));
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.password", POSTGRESQL_PASSWORD);
        System.setProperty("spring.datasource.hikari.maximum-pool-size", String.valueOf(10));
    }

    private static PostgresqlTestContainersExtension getInstance() {
        if (container == null) {
            container = new PostgresqlTestContainersExtension();
        }
        return container;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
