package com.simonjamesrowe.component.test.redis;


import com.simonjamesrowe.component.test.TestContainersExtension;
import org.apache.commons.lang3.StringUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.vault.VaultContainer;

import java.util.UUID;

/**
 * Runs test containers for Redis
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class RedisTestContainersInitializer {

    private static int redisPort;
    private static final String redisPassword = UUID.randomUUID().toString();
    private static boolean initialized = false;
    private static GenericContainer redisContainer;

    public RedisTestContainersInitializer(WithRedisContainer withRedisContainer) {
        if (!initialized) {
            redisContainer = new GenericContainer("redis:5.0.5-alpine");
            redisContainer.withExposedPorts(6379);
            redisContainer.withCommand("redis-server", "--requirepass", redisPassword);
            redisContainer.setWaitStrategy(Wait.forListeningPort());
            initialize();
        }
    }

    public void initialize() {
        if (!initialized) {
            TestContainersExtension.startContainer(redisContainer);
            redisPort = redisContainer.getMappedPort(6379);
        }
        initialized = true;
        System.setProperty("spring.redis.host", DockerClientFactory.instance().dockerHostIpAddress());
        System.setProperty("spring.redis.port", String.valueOf(redisPort));
        System.setProperty("spring.redis.password", redisPassword);
    }
}
