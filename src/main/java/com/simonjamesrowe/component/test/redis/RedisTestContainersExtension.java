package com.simonjamesrowe.component.test.redis;


import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

/**
 * Runs test containers for Redis
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class RedisTestContainersExtension extends GenericContainer implements BeforeAllCallback {

    private static final String redisPassword = UUID.randomUUID().toString();
    private static final boolean initialized = false;
    private static RedisTestContainersExtension container;

    public RedisTestContainersExtension() {
        super("redis:5.0.5-alpine");
        withExposedPorts(6379);
        withCommand("redis-server", "--requirepass", redisPassword);
        setWaitStrategy(Wait.forListeningPort());
    }

    public void start() {
        super.start();
        System.setProperty("spring.redis.host", DockerClientFactory.instance().dockerHostIpAddress());
        System.setProperty("spring.redis.port", String.valueOf(container.getMappedPort(6379)));
        System.setProperty("spring.redis.password", redisPassword);
    }

    private static RedisTestContainersExtension getInstance() {
        if (container == null) {
            container = new RedisTestContainersExtension();
        }
        return container;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
