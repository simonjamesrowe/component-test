package com.simonjamesrowe.component.test.kafka;

import io.github.embeddedkafka.EmbeddedKafka;
import io.github.embeddedkafka.EmbeddedKafkaConfig;
import io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfigImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import scala.collection.immutable.HashMap;

@Slf4j
public final class SingletonEmbeddedKafkaExtension implements BeforeAllCallback {

    public static final int NUMBER_OF_THREADS = 20;
    public static final int MAX_PORT = 65_535;
    public static final int MIN_PORT = 10_000;
    private static java.util.HashMap<String, Object> embeddedKafkaBrokers = new java.util.HashMap<>();

    public void start(final SingletonEmbeddedKafka singletonEmbeddedKafka) {
        synchronized (SingletonEmbeddedKafkaExtension.class) {
            if (!embeddedKafkaBrokers.containsKey(singletonEmbeddedKafka.name())) {
                if (singletonEmbeddedKafka.schemaRegistry()) {
                    startKafkaWithSchemaRegistry(singletonEmbeddedKafka);
                } else {
                    startKafkaWithoutSchemaRegistry(singletonEmbeddedKafka);
                }
            }
        }
    }

    private void startKafkaWithSchemaRegistry(final SingletonEmbeddedKafka singletonEmbeddedKafka) {
        final int zkPort = randomPort();
        final int kafkaPort = randomPort();
        final int schemaRegistryPort = randomPort();
        final io.github.embeddedkafka.schemaregistry.EmbeddedKafkaConfig kafkaConfig =
            new EmbeddedKafkaConfigImpl(kafkaPort, zkPort,
                                        schemaRegistryPort,
                                        new HashMap<>(),
                                        new HashMap<>(),
                                        new HashMap<>(),
                                        new HashMap<>()) {
                @Override
                public int numberOfThreads() {
                    return NUMBER_OF_THREADS;
                }
            };
        embeddedKafkaBrokers.put(singletonEmbeddedKafka.name(), io.github.embeddedkafka.schemaregistry.EmbeddedKafka.start(kafkaConfig));
        final String boostrapServers = String.format("localhost:%s", kafkaPort);
        System.setProperty(singletonEmbeddedKafka.bootstrapServerProperty(), boostrapServers);
        final String schemaRegistryUrl = String.format("http://localhost:%s", schemaRegistryPort);
        System.setProperty(singletonEmbeddedKafka.schemaRegistryProperty(),
                           schemaRegistryUrl);
        LOG.info("Started Embedded Kafka with properties  {}={}, {}={}", singletonEmbeddedKafka.bootstrapServerProperty(), boostrapServers,
                 singletonEmbeddedKafka.schemaRegistryProperty(), schemaRegistryUrl);
    }

    private void startKafkaWithoutSchemaRegistry(final SingletonEmbeddedKafka singletonEmbeddedKafka) {
        final int zkPort = randomPort();
        final int kafkaPort = randomPort();
        final EmbeddedKafkaConfig kafkaConfig = new io.github.embeddedkafka.EmbeddedKafkaConfigImpl(
            kafkaPort, zkPort, new HashMap<>(), new HashMap<>(), new HashMap<>()) {

            @Override
            public int numberOfThreads() {
                return NUMBER_OF_THREADS;
            }
        };
        embeddedKafkaBrokers.put(singletonEmbeddedKafka.name(), EmbeddedKafka.start(kafkaConfig));
        final String boostrapServers = String.format("localhost:%s", kafkaPort);
        System.setProperty(singletonEmbeddedKafka.bootstrapServerProperty(), boostrapServers);

        LOG.info("Started Embedded Kafka with properties {}={}", singletonEmbeddedKafka.bootstrapServerProperty(), boostrapServers);
    }

    private int randomPort() {
        final int randomPort = new Random().nextInt(MAX_PORT);
        if (randomPort >= MIN_PORT) {
            if (isTcpPortAvailable(randomPort)) {
                return randomPort;
            }
        }
        return randomPort();
    }

    public static boolean isTcpPortAvailable(final int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        Arrays.stream(context.getTestClass().get().getAnnotations())
            .filter(a -> a instanceof SingletonEmbeddedKafka)
            .map(a -> (SingletonEmbeddedKafka) a)
            .findFirst()
            .ifPresent(this::start);

        Arrays.stream(context.getTestClass().get().getAnnotations())
            .filter(a -> a instanceof SingletonEmbeddedKafkas).map(a -> (SingletonEmbeddedKafkas) a)
            .findFirst()
            .ifPresent(kafkas -> Arrays.stream(kafkas.value()).forEach(this::start));
    }


}
