package com.simonjamesrowe.component.test.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@SingletonEmbeddedKafka
@SingletonEmbeddedKafka(
    name = "test",
    bootstrapServerProperty = "test-spring.kafka.bootstrap-servers",
    schemaRegistry = true,
    schemaRegistryProperty = "test-schema.registry.url")
@JsonTest
@Import(KafkaAutoConfiguration.class)
class SingletonEmbeddedKafkaTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Value("${test-spring.kafka.bootstrap-servers}")
    String bootstrapServer;

    @Value("${test-schema.registry.url}")
    String schemaRegistryUrl;

    @SneakyThrows
    @Test
    void testSingletonEmbeddedKafkaWithoutSchemaRegistry() {
        final String message = "Test message1";
        final SendResult<String, String> result = kafkaTemplate.send("topic1", message).get(10, TimeUnit.SECONDS);
        assertThat(result).isNotNull();
        assertThat(result.getProducerRecord().value()).isEqualTo(message);
    }

    @SneakyThrows
    @Test
    void testSingletonEmbeddedKafkaWithSchemaRegistry() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put("schema.registry.url", schemaRegistryUrl);
        final KafkaTemplate<String, Object> testKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        final String message = "Test message2";
        final SendResult<String, Object> result = testKafkaTemplate.send("topic2", message).get(10, TimeUnit.SECONDS);
        assertThat(result).isNotNull();
        assertThat(result.getProducerRecord().value()).isEqualTo(message);
    }
}